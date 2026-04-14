package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.MovementUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * REFACTORED (Legitimacy Scan):
 *
 * VI PHẠM CŨ (Input — Rotation):
 *   Dòng cũ: mc.thePlayer.rotationYaw += diff * s;
 *   → Cộng trực tiếp delta vào rotationYaw không qua GCD/clamp.
 *   → Nếu speed cao, delta có thể vượt 30°/tick — server phát hiện snap rotation.
 *
 * SỬA:
 *   - Tính delta, clamp tối đa MAX_ROTATION_DELTA_PER_TICK (30°/tick).
 *   - Áp dụng GCD qua MovementUtils.applyGCD() để mô phỏng mouse handler.
 *   - Công thức A (cũ): rotationYaw += diff * s (không giới hạn)
 *   - Công thức B (mới): applyGCD(clamp(diff * s, -30, 30)) mỗi tick
 */
public class AimAssistMod extends Module {

    public final NumberSetting fov   = addSetting(new NumberSetting("FOV",   "AimAssist field of view", 10f, 180f, 5f,  45f));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Rotation speed x100",     5f,  80f,  1f,  15f));

    private boolean registered = false;
    private float cachedFov = -1;
    private final double[] circleX = new double[73];
    private final double[] circleY = new double[73];

    public AimAssistMod() {
        super("AimAssist", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(this);
            registered = true;
        }
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null || !Mouse.isButtonDown(0)) return;

        EntityPlayer target = null;
        double closestDist  = 4.5;

        EntityPlayer[] snapshot = mc.theWorld.playerEntities.toArray(new EntityPlayer[0]);
        for (EntityPlayer e : snapshot) {
            if (e == mc.thePlayer || e.isDead) continue;
            double dist = mc.thePlayer.getDistanceToEntity(e);
            if (dist < closestDist && isInFov(e, fov.getValue())) {
                closestDist = dist;
                target = e;
            }
        }

        if (target == null) return;

        double dx   = target.posX - mc.thePlayer.posX;
        double dz   = target.posZ - mc.thePlayer.posZ;
        double dy   = (target.posY + target.getEyeHeight() * 0.7)
                    - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);

        float targetYaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float targetPitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);

        float s = speed.getValue() / 100f;

        // THAY ĐỔI: Clamp delta tối đa 30°/tick + GCD thay vì cộng trực tiếp
        // Công thức A (cũ): rotationYaw += diff * s (không giới hạn)
        // Công thức B (mới): clamp(diff * s, -30, 30) → applyGCD()
        float diffYaw   = MathHelper.wrapAngleTo180_float(targetYaw   - mc.thePlayer.rotationYaw)   * s;
        float diffPitch = MathHelper.wrapAngleTo180_float(targetPitch - mc.thePlayer.rotationPitch) * s;

        diffYaw   = MathHelper.clamp_float(diffYaw,   -MovementUtils.MAX_ROTATION_DELTA_PER_TICK, MovementUtils.MAX_ROTATION_DELTA_PER_TICK);
        diffPitch = MathHelper.clamp_float(diffPitch, -MovementUtils.MAX_ROTATION_DELTA_PER_TICK, MovementUtils.MAX_ROTATION_DELTA_PER_TICK);

        float[] smoothed = MovementUtils.applyGCD(
                mc.thePlayer.rotationYaw   + diffYaw,
                mc.thePlayer.rotationPitch + diffPitch,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch);

        mc.thePlayer.rotationYaw   = smoothed[0];
        mc.thePlayer.rotationPitch = MathHelper.clamp_float(smoothed[1], -90f, 90f);
    }

    private boolean isInFov(EntityPlayer e, float currentFov) {
        double dx  = e.posX - mc.thePlayer.posX;
        double dz  = e.posZ - mc.thePlayer.posZ;
        float  yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float  diff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        return Math.abs(diff) <= currentFov / 2.0f;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled() || mc.gameSettings.thirdPersonView != 0) return;
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int   cx     = sr.getScaledWidth()  / 2;
        int   cy     = sr.getScaledHeight() / 2;
        float radius = (fov.getValue() / mc.gameSettings.fovSetting) * (sr.getScaledWidth() / 2.0f);

        if (fov.getValue() != cachedFov) {
            cachedFov = fov.getValue();
            for (int i = 0; i <= 72; i++) {
                double a = i * Math.PI / 36.0;
                circleX[i] = Math.sin(a);
                circleY[i] = Math.cos(a);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 72; i++) {
            GL11.glVertex2d(cx + circleX[i] * radius, cy + circleY[i] * radius);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
