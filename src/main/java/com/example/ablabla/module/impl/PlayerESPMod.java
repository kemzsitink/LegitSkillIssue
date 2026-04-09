package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PlayerESPMod extends Module {

    private boolean registered = false;

    public PlayerESPMod() {
        super("PlayerESP");
    }

    @Override
    protected void onEnable() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(this);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.theWorld == null || mc.thePlayer == null) return;

        float pt = event.partialTicks;

        // Interpolated camera position
        double cx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double cy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double cz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-cx, -cy, -cz);

        // Disable depth test → see through walls
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glLineWidth(1.5f);

        for (Object obj : mc.theWorld.playerEntities) {
            if (!(obj instanceof EntityPlayer)) continue;
            EntityPlayer p = (EntityPlayer) obj;
            if (p == mc.thePlayer) continue;
            // Show invisible players too — ignore isInvisible()

            // Interpolated entity position
            double ex = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt;
            double ey = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt;
            double ez = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt;

            AxisAlignedBB bb = new AxisAlignedBB(
                ex - 0.3, ey,       ez - 0.3,
                ex + 0.3, ey + 1.8, ez + 0.3
            );

            // Color: red if enemy, green if teammate (no team = red)
            boolean sameTeam = mc.thePlayer.getTeam() != null
                    && mc.thePlayer.getTeam().equals(p.getTeam());
            float r = sameTeam ? 0.2f : 1.0f;
            float g = sameTeam ? 1.0f : 0.2f;
            float b = 0.2f;

            drawBoundingBox(bb, r, g, b, 0.8f);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void drawBoundingBox(AxisAlignedBB bb, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        GlStateManager.color(r, g, b, a);

        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        // Bottom face
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        tess.draw();

        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        // Top face
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        // Vertical edges
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();
    }
}
