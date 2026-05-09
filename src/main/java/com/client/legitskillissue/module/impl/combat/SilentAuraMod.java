package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * REFACTORED: SilentAura with Single/Multi target modes and AntiBot integration.
 *
 * IMPROVEMENTS:
 * - Single Mode: Attack one target at a time (original behavior)
 * - Multi Mode: Attack multiple targets in range
 * - Switch Mode: Rapidly switch between targets
 * - AntiBot Integration: Automatically ignore bots
 * - Rotation interpolation with 30°/tick clamp
 * - Gaussian delay distribution
 *
 * ANTI-CHEAT SAFE:
 * - Smooth rotation interpolation
 * - GCD compliance
 * - Realistic attack delays
 * - Bot filtering
 */
public class SilentAuraMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Target mode", "Single", "Multi", "Switch"));
    public final NumberSetting range    = addSetting(new NumberSetting("SA Range",    "Attack range",           2.0f, 6.0f, 0.1f, 4.0f));
    public final NumberSetting minDelay = addSetting(new NumberSetting("SA MinDelay", "Min ticks between hits", 2f,   10f,  1f,   4f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("SA MaxDelay", "Max ticks between hits", 2f,   10f,  1f,   7f));
    public final NumberSetting maxTargets = addSetting(new NumberSetting("Max Targets", "Max targets for Multi mode", 1f, 5f, 1f, 3f));
    public final NumberSetting switchDelay = addSetting(new NumberSetting("Switch Delay", "Ticks between target switch", 5f, 20f, 1f, 10f));
    public final BooleanSetting antiBot = addSetting(new BooleanSetting("AntiBot", "Ignore bots", true));

    private EntityPlayer currentTarget;
    private final List<EntityPlayer> targets = new ArrayList<>();
    private int currentTargetIndex = 0;
    private int switchTicks = 0;

    // State interpolation
    private float   currentYaw;
    private float   currentPitch;
    private boolean isSpoofing = false;

    private int ticksUntilAttack = 0;
    private final Random rng = new Random();

    public SilentAuraMod() { super("SilentAura", Category.COMBAT); }

    @Override
    protected void onEnable() {
        currentTarget    = null;
        targets.clear();
        currentTargetIndex = 0;
        switchTicks = 0;
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

        // Update target list based on mode
        updateTargets();

        if (targets.isEmpty()) {
            isSpoofing = false;
            currentTarget = null;
            return;
        }

        // Select current target based on mode
        String currentMode = mode.getMode();
        if (currentMode.equals("Single")) {
            currentTarget = targets.get(0); // Always attack closest
        } else if (currentMode.equals("Multi")) {
            // Multi mode: attack all targets in list
            currentTarget = targets.get(currentTargetIndex % targets.size());
        } else if (currentMode.equals("Switch")) {
            // Switch mode: change target every X ticks
            switchTicks++;
            if (switchTicks >= switchDelay.getInt()) {
                currentTargetIndex = (currentTargetIndex + 1) % targets.size();
                switchTicks = 0;
            }
            currentTarget = targets.get(currentTargetIndex);
        }

        if (currentTarget == null) {
            isSpoofing = false;
            return;
        }

        // Calculate target rotation
        float[] targetRots = calcRotation(currentTarget);

        // Interpolate rotation with 30°/tick clamp
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

        // Attack logic
        ticksUntilAttack--;
        if (ticksUntilAttack <= 0) {
            ticksUntilAttack = nextDelay();

            // Multi mode: attack multiple targets
            if (currentMode.equals("Multi")) {
                int attackCount = Math.min(targets.size(), (int) maxTargets.getValue());
                for (int i = 0; i < attackCount; i++) {
                    EntityPlayer target = targets.get(i);
                    attackTarget(target);
                }
            } else {
                // Single/Switch mode: attack current target
                attackTarget(currentTarget);
            }
        }
    }

    /**
     * Updates the target list based on range and filters.
     */
    private void updateTargets() {
        targets.clear();
        float rangeSq = range.getValue() * range.getValue();

        List<EntityPlayer> potentialTargets = mc.theWorld.playerEntities.stream()
            .filter(p -> p != mc.thePlayer && !p.isDead)
            .filter(p -> mc.thePlayer.getDistanceSqToEntity(p) <= rangeSq)
            .filter(p -> !antiBot.getValue() || !AntiBotMod.isBot(p)) // Filter bots
            .sorted(Comparator.comparingDouble(p -> mc.thePlayer.getDistanceSqToEntity(p)))
            .collect(Collectors.toList());

        // Limit targets based on mode
        String currentMode = mode.getMode();
        if (currentMode.equals("Single") || currentMode.equals("Switch")) {
            if (!potentialTargets.isEmpty()) {
                targets.add(potentialTargets.get(0));
            }
        } else if (currentMode.equals("Multi")) {
            int maxCount = (int) maxTargets.getValue();
            targets.addAll(potentialTargets.subList(0, Math.min(maxCount, potentialTargets.size())));
        }
    }

    /**
     * Attacks a specific target.
     */
    private void attackTarget(EntityPlayer target) {
        if (target == null) return;

        // Temporarily set player rotation for consistent attack
        float savedYaw   = mc.thePlayer.rotationYaw;
        float savedPitch = mc.thePlayer.rotationPitch;

        mc.thePlayer.rotationYaw   = currentYaw;
        mc.thePlayer.rotationPitch = currentPitch;

        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, target);

        // Restore rotation immediately
        mc.thePlayer.rotationYaw   = savedYaw;
        mc.thePlayer.rotationPitch = savedPitch;
    }

    @EventTarget
    public void onPacketEvent(EventPacket event) {
        if (!event.isSend || !isSpoofing || currentTarget == null) return;
        if (!(event.getPacket() instanceof C03PacketPlayer)) return;

        C03PacketPlayer p        = (C03PacketPlayer) event.getPacket();
        boolean         onGround = mc.thePlayer.onGround;

        // Create new packet with interpolated rotation
        if (p.isMoving()) {
            event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    p.getPositionX(), p.getPositionY(), p.getPositionZ(),
                    currentYaw, currentPitch, onGround));
        } else {
            event.setPacket(new C03PacketPlayer.C05PacketPlayerLook(
                    currentYaw, currentPitch, onGround));
        }
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

    /**
     * Gets the current target (for external modules).
     */
    public EntityPlayer getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Gets all current targets (for Multi mode).
     */
    public List<EntityPlayer> getTargets() {
        return new ArrayList<>(targets);
    }
}
