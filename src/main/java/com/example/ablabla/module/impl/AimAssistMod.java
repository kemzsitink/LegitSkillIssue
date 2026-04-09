package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AimAssistMod extends Module {

    public float fov = 45.0f;
    public float speed = 15f;  // rotation speed * 100 (15 = 0.15f)
    public boolean aimLock = false;

    private boolean registeredToForge = false;

    public AimAssistMod() {
        super("AimAssist");
    }

    @Override
    protected void onEnable() {
        if (!registeredToForge) {
            MinecraftForge.EVENT_BUS.register(this);
            registeredToForge = true;
        }
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null || !Mouse.isButtonDown(0)) return;

        Entity target = null;
        double range = 4.5;
        int targetCount = 0;
        double closestDist = range;

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof Entity)) continue;
            Entity e = (Entity) obj;
            if (!isValidTarget(e)) continue;

            double dist = mc.thePlayer.getDistanceToEntity(e);
            if (dist < range) {
                targetCount++;
                if (!aimLock && dist < closestDist) {
                    closestDist = dist;
                    target = e;
                } else if (aimLock) {
                    target = e;
                }
            }
        }

        if (aimLock && targetCount != 1) return;
        if (target == null) return;
        if (!isInFov(target, fov)) return;

        double dx = target.posX - mc.thePlayer.posX;
        double dz = target.posZ - mc.thePlayer.posZ;
        double dy = (target.posY + target.getEyeHeight() * 0.7)
                  - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(dx * dx + dz * dz);

        float yaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);

        float yawDiff   = MathHelper.wrapAngleTo180_float(yaw   - mc.thePlayer.rotationYaw);
        float pitchDiff = MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);

        float speed = aimLock ? 0.45f : (this.speed / 100f);
        mc.thePlayer.rotationYaw   += yawDiff   * speed;
        mc.thePlayer.rotationPitch += pitchDiff * speed;
    }

    private boolean isValidTarget(Entity e) {
        if (!(e instanceof EntityPlayer)) return false;
        if (e == mc.thePlayer || e.isInvisible() || e.isDead) return false;
        if (mc.thePlayer.isDead) return false;
        return true;
    }

    private boolean isInFov(Entity e, float currentFov) {
        double dx = e.posX - mc.thePlayer.posX;
        double dz = e.posZ - mc.thePlayer.posZ;
        float yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float diff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        return Math.abs(diff) <= currentFov / 2.0f;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!isEnabled() || mc.gameSettings.thirdPersonView != 0) return;
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int cx = sr.getScaledWidth()  / 2;
        int cy = sr.getScaledHeight() / 2;
        float radius = (fov / mc.gameSettings.fovSetting) * (sr.getScaledWidth() / 2.0f);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i += 5) {
            double angle = i * Math.PI / 180.0;
            GL11.glVertex2d(cx + Math.sin(angle) * radius, cy + Math.cos(angle) * radius);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
