package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender2D;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.module.impl.combat.SilentAuraMod;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class TargetHUDMod extends Module {

    private float healthAnim = 0;

    public TargetHUDMod() {
        super("TargetHUD", Category.RENDER);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        SilentAuraMod aura = ModuleManager.INSTANCE.getModule(SilentAuraMod.class);
        EntityPlayer target = null;
        
        if (aura != null && aura.isEnabled() && aura.getCurrentTarget() != null) {
            target = aura.getCurrentTarget();
        }

        // If no aura target, fallback to mouse over if it's a player
        if (target == null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
            target = (EntityPlayer) mc.objectMouseOver.entityHit;
        }

        if (target == null) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();

        // Draw HUD at the center right
        int x = width / 2 + 30;
        int y = height / 2 + 10;

        int hudWidth = 140;
        int hudHeight = 45;

        // Background
        RenderUtils.drawRoundedRect(x, y, x + hudWidth, y + hudHeight, 4, new Color(20, 20, 20, 200).getRGB());

        // Name
        mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 40, y + 5, -1);

        // Health
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float healthPct = Math.min(1.0f, Math.max(0.0f, health / maxHealth));

        // Smooth animation for health bar
        if (healthAnim != healthPct) {
            float diff = healthPct - healthAnim;
            healthAnim += diff * 0.1f;
            if (Math.abs(diff) < 0.01f) healthAnim = healthPct;
        }

        // Health Bar Background
        RenderUtils.drawRect(x + 40, y + 20, x + hudWidth - 10, y + 26, new Color(40, 40, 40).getRGB());
        
        // Health Bar
        Color healthColor = getHealthColor(healthAnim);
        RenderUtils.drawRect(x + 40, y + 20, x + 40 + (hudWidth - 50) * healthAnim, y + 26, healthColor.getRGB());

        // Health Text
        String hpText = String.format("%.1f \u2764", health / 2.0f); // Display in hearts
        float hpScale = 0.5f;
        GL11.glPushMatrix();
        GL11.glScalef(hpScale, hpScale, hpScale);
        float hpX = (x + 40 + (hudWidth - 50) / 2.0f) / hpScale - mc.fontRendererObj.getStringWidth(hpText) / 2.0f;
        float hpY = (y + 21) / hpScale;
        mc.fontRendererObj.drawStringWithShadow(hpText, hpX, hpY, -1);
        GL11.glPopMatrix();

        // Distance
        String distText = String.format("Dist: %.1f", mc.thePlayer.getDistanceToEntity(target));
        mc.fontRendererObj.drawStringWithShadow(distText, x + 40, y + 30, new Color(180, 180, 180).getRGB());
        
        // Armor (Simple calculation)
        int armor = target.getTotalArmorValue();
        String armorText = String.format("Armor: %d", armor);
        mc.fontRendererObj.drawStringWithShadow(armorText, x + 90, y + 30, new Color(180, 180, 180).getRGB());

        // Player Head
        drawFace(x + 5, y + 5, 30, 30, target);
    }

    private Color getHealthColor(float pct) {
        if (pct > 0.75f) return new Color(50, 255, 50);
        if (pct > 0.5f) return new Color(255, 255, 50);
        if (pct > 0.25f) return new Color(255, 150, 50);
        return new Color(255, 50, 50);
    }

    private void drawFace(int x, int y, int w, int h, EntityPlayer target) {
        try {
            net.minecraft.client.network.NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
            if (info != null && info.getLocationSkin() != null) {
                mc.getTextureManager().bindTexture(info.getLocationSkin());
                Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, w, h, 64.0F, 64.0F);
                // Hat layer
                Gui.drawScaledCustomSizeModalRect(x, y, 40.0F, 8.0F, 8, 8, w, h, 64.0F, 64.0F);
            } else {
                RenderUtils.drawRect(x, y, x + w, y + h, new Color(60, 60, 60).getRGB());
            }
        } catch (Exception e) {
            RenderUtils.drawRect(x, y, x + w, y + h, new Color(60, 60, 60).getRGB());
        }
    }
}
