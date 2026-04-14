package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

import java.util.Random;

/**
 * REFACTORED (MCP-919): SilentAura — Rotation Interpolation Compliant
 *
 * VI PHẠM CŨ:
 *   Dòng cũ (SilentAuraMod.java:55-57): YAW.setFloat(packet, spoofYaw) qua reflection
 *   → Set yaw/pitch trực tiếp vào packet không qua interpolation, vi phạm chỉ thị §3.
 *   → Không giới hạn delta góc, có thể thay đổi > 30°/tick.
 *
 * THAY ĐỔI:
 *   - Bỏ hoàn toàn reflection (YAW, PITCH, ROTATING fields).
 *   - Dùng interpolation state (currentYaw, currentPitch) được cập nhật mỗi tick
 *     với clamp 30°/tick qua MovementUtils.applyGCD().
 *   - Chuyển sang @EventTarget EventPacket để có thể gọi event.setPacket().
 *
 *   Công thức A (cũ): YAW.setFloat(packet, spoofYaw) — set trực tiếp qua reflection.
 *   Công thức B (mới): Interpolate currentYaw → targetYaw với clamp 30°/tick,
 *                      tạo packet mới với rotation đã interpolate qua event.setPacket().
 */
public class SilentAuraMod extends Module {

    public final NumberSetting range    = addSetting(new NumberSetting("SA Range",    "Attack range",           2.0f, 6.0f, 0.1f, 4.0f));
    public final NumberSetting minDelay = addSetting(new NumberSetting("SA MinDelay", "Min ticks between hits", 2f,   10f,  1f,   4f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("SA MaxDelay", "Max ticks between hits", 2f,   10f,  1f,   7f));

    private EntityPlayer target;

    // State interpolation — không set trực tiếp vào packet qua reflection
    private float   currentYaw;
    private float   currentPitch;
    private boolean isSpoofing = false;

    private int ticksUntilAttack = 0;
    private final Random rng = new Random();

    public SilentAuraMod() { super("SilentAura", Category.COMBAT); }

    @Override
    protected void onEnable() {
        target           = null;
        ticksUntilAttack = nextDelay();
        isSpoofing       = false;
        if (mc.thePlayer != null) {
            currentYaw   = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
        }
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        target = getClosestTarget();

        if (target == null) {
            isSpoofing = false;
            return;
        }

        // Tính góc mục tiêu
        float[] targetRots = calcRotation(target);

        // THAY ĐỔI: Interpolate với clamp 30°/tick thay vì set trực tiếp
        // Công thức A (cũ): spoofYaw = targetRots[0]; spoofPitch = targetRots[1]; (snap tức thì)
        // Công thức B (mới): Interpolate qua applyGCD() với clamp MAX_ROTATION_DELTA_PER_TICK
        float diffYaw   = MathHelper.wrapAngleTo180_float(targetRots[0] - currentYaw);
        float diffPitch = MathHelper.wrapAngleTo180_float(targetRots[1] - currentPitch);
        diffYaw   = MathHelper.clamp_float(diffYaw,   -MovementUtils.MAX_ROTATION_DELTA_PER_TICK, MovementUtils.MAX_ROTATION_DELTA_PER_TICK);
        diffPitch = MathHelper.clamp_float(diffPitch, -MovementUtils.MAX_ROTATION_DELTA_PER_TICK, MovementUtils.MAX_ROTATION_DELTA_PER_TICK);

        float[] interpolated = MovementUtils.applyGCD(
                currentYaw + diffYaw, currentPitch + diffPitch,
                currentYaw, currentPitch);
        currentYaw   = interpolated[0];
        currentPitch = MathHelper.clamp_float(interpolated[1], -90f, 90f);
        isSpoofing   = true;

        ticksUntilAttack--;
        if (ticksUntilAttack <= 0) {
            ticksUntilAttack = nextDelay();

            // Tạm thời set rotation thật của player về góc spoof
            // để server nhận rotation nhất quán với C03 packet
            // và client tính đúng knockback direction / critical hit
            float savedYaw   = mc.thePlayer.rotationYaw;
            float savedPitch = mc.thePlayer.rotationPitch;

            mc.thePlayer.rotationYaw   = currentYaw;
            mc.thePlayer.rotationPitch = currentPitch;

            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, target);

            // Restore ngay sau attack — player không thấy camera giật
            mc.thePlayer.rotationYaw   = savedYaw;
            mc.thePlayer.rotationPitch = savedPitch;
        }
    }

    @EventTarget
    public void onPacketEvent(EventPacket event) {
        if (!event.isSend || !isSpoofing || target == null) return;
        if (!(event.getPacket() instanceof C03PacketPlayer)) return;

        C03PacketPlayer p        = (C03PacketPlayer) event.getPacket();
        boolean         onGround = mc.thePlayer.onGround;

        // THAY ĐỔI: Tạo packet mới thay vì dùng reflection để set field
        // Công thức A (cũ): YAW.setFloat(packet, spoofYaw); PITCH.setFloat(packet, spoofPitch);
        // Công thức B (mới): Tạo C05/C06 mới với rotation đã interpolate, gọi event.setPacket()
        if (p.isMoving()) {
            event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    p.getPositionX(), p.getPositionY(), p.getPositionZ(),
                    currentYaw, currentPitch, onGround));
        } else {
            event.setPacket(new C03PacketPlayer.C05PacketPlayerLook(
                    currentYaw, currentPitch, onGround));
        }
    }

    private EntityPlayer getClosestTarget() {
        EntityPlayer closest = null;
        float        rangeSq = range.getValue() * range.getValue();
        double       best    = Double.MAX_VALUE;
        for (EntityPlayer p : mc.theWorld.playerEntities.toArray(new EntityPlayer[0])) {
            if (p == mc.thePlayer || p.isDead) continue;
            double d = mc.thePlayer.getDistanceSqToEntity(p);
            if (d < rangeSq && d < best) { best = d; closest = p; }
        }
        return closest;
    }

    private float[] calcRotation(EntityPlayer t) {
        double dx   = t.posX - mc.thePlayer.posX;
        double dz   = t.posZ - mc.thePlayer.posZ;
        double dy   = (t.posY + t.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);
        float  yaw  = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float  pitch= (float) -(Math.atan2(dy, dist) * 180.0 / Math.PI);
        return new float[]{ yaw, pitch };
    }

    private int nextDelay() {
        return minDelay.getInt() + rng.nextInt(Math.max(1, maxDelay.getInt() - minDelay.getInt() + 1));
    }
}
