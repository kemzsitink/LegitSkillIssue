package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender3D;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

/**
 * ARCHITECT RENDER: Modern 3D ESP
 * Replaced jagged lines with a clean, translucent filled box option.
 */
public class PlayerESPMod extends Module {

    public final BooleanSetting filled = addSetting(new BooleanSetting("Filled", "Draw translucent filled box", true));
    public final BooleanSetting outline = addSetting(new BooleanSetting("Outline", "Draw bounding box lines", true));

    public PlayerESPMod() {
        super("PlayerESP", Category.RENDER);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        float pt = event.getPartialTicks();

        double cx = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * pt;
        double cy = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * pt;
        double cz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * pt;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-cx, -cy, -cz);

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glLineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        if (filled.getValue()) {
            wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            for (EntityPlayer p : mc.theWorld.playerEntities) {
                if (p == mc.thePlayer || p.isDead) continue;

                double ex = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt;
                double ey = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt;
                double ez = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt;

                AxisAlignedBB bb = new AxisAlignedBB(
                    ex - 0.3, ey,       ez - 0.3,
                    ex + 0.3, ey + 2.0, ez + 0.3
                );

                boolean sameTeam = mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(p.getTeam());
                float r = sameTeam ? 0.2f : 1.0f;
                float g = sameTeam ? 1.0f : 0.2f;
                float b = 0.2f;
                float a = 0.25f;

                addBoxToBuffer(wr, bb, r, g, b, a);
            }
            tess.draw();
        }

        if (outline.getValue()) {
            for (EntityPlayer p : mc.theWorld.playerEntities) {
                if (p == mc.thePlayer || p.isDead) continue;

                double ex = p.lastTickPosX + (p.posX - p.lastTickPosX) * pt;
                double ey = p.lastTickPosY + (p.posY - p.lastTickPosY) * pt;
                double ez = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * pt;

                AxisAlignedBB bb = new AxisAlignedBB(
                    ex - 0.3, ey,       ez - 0.3,
                    ex + 0.3, ey + 2.0, ez + 0.3
                );

                boolean sameTeam = mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(p.getTeam());
                float r = sameTeam ? 0.2f : 1.0f;
                float g = sameTeam ? 1.0f : 0.2f;
                float b = 0.2f;

                drawBoundingBox(bb, r, g, b, 0.8f);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void addBoxToBuffer(WorldRenderer wr, AxisAlignedBB bb, float r, float g, float b, float a) {
        // Bottom
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        // Top
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        // X-
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        // X+
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        // Z-
        wr.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        // Z+
        wr.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
    }

    private void drawBoundingBox(AxisAlignedBB bb, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        GlStateManager.color(r, g, b, a);

        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        tess.draw();

        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        wr.pos(bb.minX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.minZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        wr.pos(bb.maxX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        wr.pos(bb.minX, bb.minY, bb.maxZ).endVertex(); wr.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();
    }
}
