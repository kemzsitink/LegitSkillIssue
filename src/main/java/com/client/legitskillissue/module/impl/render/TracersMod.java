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
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class TracersMod extends Module {

    public TracersMod() {
        super("Tracers", Category.RENDER);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (mc.theWorld == null || mc.thePlayer == null || mc.theWorld.playerEntities.isEmpty()) return;

        float pt = event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        Vec3 eyes = new Vec3(0, mc.thePlayer.getEyeHeight(), 0);
        eyes = eyes.rotatePitch(-(float) Math.toRadians(mc.thePlayer.rotationPitch));
        eyes = eyes.rotateYaw(-(float) Math.toRadians(mc.thePlayer.rotationYaw));
        
        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        // Batch start
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (EntityPlayer p : mc.theWorld.playerEntities) {
            if (p == mc.thePlayer || p.isDead || p.isInvisible()) continue;

            double ex = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt - renderPosX;
            double ey = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt - renderPosY;
            double ez = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt - renderPosZ;

            boolean isFriend = FriendManager.isFriend(p.getName());
            boolean sameTeam = mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(p.getTeam());
            
            Color color = new Color(255, 50, 50); // Red (Enemy)
            if (isFriend) color = new Color(50, 50, 255); // Blue (Friend)
            else if (sameTeam) color = new Color(50, 255, 50); // Green (Team)

            int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();

            wr.pos(eyes.xCoord, eyes.yCoord, eyes.zCoord).color(r, g, b, a).endVertex();
            wr.pos(ex, ey + p.height / 2.0, ez).color(r, g, b, a).endVertex();
        }
        
        // Batch end
        tess.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
