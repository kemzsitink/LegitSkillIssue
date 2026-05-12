package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender3D;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.FriendManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class NametagsMod extends Module {

    public NametagsMod() {
        super("Nametags", Category.RENDER);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        float pt = event.getPartialTicks();
        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        for (EntityPlayer p : mc.theWorld.playerEntities) {
            if (p == mc.thePlayer || p.isDead || p.isInvisible()) continue;

            double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt - renderPosX;
            double y = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt - renderPosY;
            double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt - renderPosZ;

            renderNametag(p, x, y, z);
        }
    }

    private void renderNametag(EntityPlayer player, double x, double y, double z) {
        double tempY = y + (player.isSneaking() ? 0.5D : 0.7D);
        double distance = mc.thePlayer.getDistanceToEntity(player);
        double scale = Math.max(distance / 5.0, 2.0);
        scale /= 100.0;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) tempY + player.height + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        String name = player.getName();
        if (FriendManager.isFriend(name)) {
            name = "\u00A7b[F] \u00A7r" + name;
        }

        String health = String.format(" \u00A7c%.1f\u2764", player.getHealth() / 2.0f);
        String text = name + health;
        int width = mc.fontRendererObj.getStringWidth(text) / 2;

        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) (-width - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (-width - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (width + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (width + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        mc.fontRendererObj.drawStringWithShadow(text, -width, 0, -1);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
