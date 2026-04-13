package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AimAssistMod extends Module {

    public final NumberSetting fov   = addSetting(new NumberSetting("FOV",   "AimAssist field of view",    10f, 180f, 5f,  45f));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Rotation speed x100",        5f,  80f,  1f,  15f));

    private boolean registered = false;
    private float cachedFov = -1;
    private final double[] circleX = new double[73];
    private final double[] circleY = new double[73];

    public AimAssistMod() {
        super("AimAssist");
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

        // Use playerEntities (smaller list) instead of loadedEntityList
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

        float yaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);

        float s = speed.getValue() / 100f;
        mc.thePlayer.rotationYaw   += MathHelper.wrapAngleTo180_float(yaw   - mc.thePlayer.rotationYaw)   * s;
        mc.thePlayer.rotationPitch += MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) * s;
    }

    private boolean isInFov(EntityPlayer e, float currentFov) {        double dx   = e.posX - mc.thePlayer.posX;
        double dz   = e.posZ - mc.thePlayer.posZ;
        float  yaw  = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
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

        // Rebuild circle cache only when FOV changes
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
