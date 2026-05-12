package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender3D;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class BreadcrumbsMod extends Module {

    public final NumberSetting maxPoints = addSetting(new NumberSetting("Max Points", "Maximum trail points", 100f, 1000f, 10f, 500f));
    public final BooleanSetting clearOnDisable = addSetting(new BooleanSetting("Clear On Disable", "Clear trail when disabled", true));

    private final List<double[]> path = new LinkedList<>();

    public BreadcrumbsMod() {
        super("Breadcrumbs", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        path.clear();
    }

    @Override
    protected void onDisable() {
        if (clearOnDisable.getValue()) {
            path.clear();
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer == null) return;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.getEntityBoundingBox().minY;
        double z = mc.thePlayer.posZ;

        if (path.isEmpty()) {
            path.add(new double[]{x, y, z});
        } else {
            double[] last = path.get(path.size() - 1);
            if (last[0] != x || last[1] != y || last[2] != z) {
                path.add(new double[]{x, y, z});
            }
        }

        while (path.size() > maxPoints.getInt()) {
            path.remove(0);
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (path.size() < 2) return;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0f);

        double renderPosX = mc.getRenderManager().viewerPosX;
        double renderPosY = mc.getRenderManager().viewerPosY;
        double renderPosZ = mc.getRenderManager().viewerPosZ;

        GL11.glBegin(GL11.GL_LINE_STRIP);
        
        float hue = 0f;
        for (double[] pos : path) {
            int color = hsvToRgb(hue, 0.8f, 1.0f);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            GL11.glColor4f(r, g, b, 1.0f);
            GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);
            
            hue += 0.01f;
            if (hue > 1.0f) hue = 0f;
        }
        
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        
        GlStateManager.resetColor();
    }

    private static int hsvToRgb(float h, float s, float v) {
        int hi = (int)(h * 6) % 6;
        float f = h * 6 - (int)(h * 6);
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        float r, g, b;
        switch (hi) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return (255 << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }
}
