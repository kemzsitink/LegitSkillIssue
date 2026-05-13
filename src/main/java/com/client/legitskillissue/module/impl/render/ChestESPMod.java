package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender3D;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ChestESPMod extends Module {

    public ChestESPMod() {
        super("ChestESP", Category.RENDER);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (mc.theWorld == null || mc.thePlayer == null || mc.theWorld.loadedTileEntityList.isEmpty()) return;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glLineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        // Pass 1: Draw Boxes (Quads)
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (Object obj : mc.theWorld.loadedTileEntityList) {
            if (obj instanceof TileEntityChest || obj instanceof TileEntityEnderChest) {
                BlockPos pos = ((net.minecraft.tileentity.TileEntity) obj).getPos();
                double x = pos.getX() - renderPosX;
                double y = pos.getY() - renderPosY;
                double z = pos.getZ() - renderPosZ;
                AxisAlignedBB bb = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 0.9375, y + 0.875, z + 0.9375);
                
                Color c = (obj instanceof TileEntityEnderChest) ? new Color(200, 0, 255, 60) : new Color(255, 150, 0, 60);
                addBoxToBatch(wr, bb, c);
            }
        }
        tess.draw();

        // Pass 2: Draw Outlines (Lines)
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (Object obj : mc.theWorld.loadedTileEntityList) {
            if (obj instanceof TileEntityChest || obj instanceof TileEntityEnderChest) {
                BlockPos pos = ((net.minecraft.tileentity.TileEntity) obj).getPos();
                double x = pos.getX() - renderPosX;
                double y = pos.getY() - renderPosY;
                double z = pos.getZ() - renderPosZ;
                AxisAlignedBB bb = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 0.9375, y + 0.875, z + 0.9375);
                
                Color c = (obj instanceof TileEntityEnderChest) ? new Color(200, 0, 255, 255) : new Color(255, 150, 0, 255);
                addOutlineToBatch(wr, bb, c);
            }
        }
        tess.draw();

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    
    private void addBoxToBatch(WorldRenderer wr, AxisAlignedBB bb, Color c) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue(), a = c.getAlpha();
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
    }
    
    private void addOutlineToBatch(WorldRenderer wr, AxisAlignedBB bb, Color c) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue(), a = c.getAlpha();
        // Bottom
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        // Top
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        // Verticals
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
    }
}
