package com.client.legitskillissue.gui;

import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.utils.TpsTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ARCHITECT HUD: Clean, modern overlay without custom fonts.
 */
public class HudRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, Float> slideOffsets = new HashMap<>();
    private List<Module> sortedModules = null;
    private float chromaHue = 0f;

    private long lastWatermarkUpdate = 0;
    private String cachedWatermark = "";
    private int lastFps, lastPing, lastEntities;
    private float lastTps;
    private double lastBps;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.thePlayer == null || mc.gameSettings.showDebugInfo) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int screenW = sr.getScaledWidth();

        chromaHue = (chromaHue + 0.002f) % 1.0f;

        drawWatermark();
        drawArrayList(screenW);
    }

    private void drawWatermark() {
        int fps = Minecraft.getDebugFPS();
        int ping = getPing();
        float tps = TpsTracker.INSTANCE.getTps();
        double bps = calculateBPS();
        int entities = mc.theWorld.loadedEntityList.size();

        if (System.currentTimeMillis() - lastWatermarkUpdate > 100 || 
            fps != lastFps || ping != lastPing || tps != lastTps || 
            Math.abs(bps - lastBps) > 0.1 || entities != lastEntities) {
            
            lastFps = fps;
            lastPing = ping;
            lastTps = tps;
            lastBps = bps;
            lastEntities = entities;
            lastWatermarkUpdate = System.currentTimeMillis();
            
            StringBuilder sb = new StringBuilder(64);
            sb.append("LegitSkillIssue \u00A77| \u00A7fFPS: ").append(fps)
              .append(" \u00A77| \u00A7fTPS: ").append(String.format("%.1f", tps))
              .append(" \u00A77| \u00A7fPing: ").append(ping).append("ms")
              .append(" \u00A77| \u00A7fBPS: ").append(String.format("%.2f", bps))
              .append(" \u00A77| \u00A7fEnt: ").append(entities);
            cachedWatermark = sb.toString();
        }

        int color = hsvToRgb(chromaHue, 0.6f, 1.0f);
        int textW = mc.fontRendererObj.getStringWidth(cachedWatermark);
        
        // Background rect for watermark
        Gui.drawRect(2, 2, 6 + textW, 14, 0x99000000);
        // Top accent line
        Gui.drawRect(2, 2, 6 + textW, 3, color);

        mc.fontRendererObj.drawString(cachedWatermark, 4, 5, color);
    }

    private void drawArrayList(int screenW) {
        if (sortedModules == null) {
            sortedModules = new ArrayList<>(ModuleManager.INSTANCE.getModules());
            sortedModules.sort(Comparator.comparingInt(m -> -mc.fontRendererObj.getStringWidth(m.getName())));
        }

        int y = 2;
        int fontH = mc.fontRendererObj.FONT_HEIGHT + 2;

        for (int i = 0; i < sortedModules.size(); i++) {
            Module m = sortedModules.get(i);
            String name = m.getName();

            Float offsetObj = slideOffsets.get(name);
            float current = offsetObj != null ? offsetObj : 1.0f;
            float target = m.isEnabled() ? 0.0f : 1.0f;
            
            if (current != target) {
                float next = current + (target - current) * 0.15f;
                if (Math.abs(next - target) < 0.005f) next = target;
                slideOffsets.put(name, next);
                current = next;
            }

            if (current >= 0.999f) continue;

            int textW = mc.fontRendererObj.getStringWidth(name);
            int slideX = (int)(textW * current); 
            int drawX = screenW - textW - 4 + slideX;

            float hue = (chromaHue + i * 0.05f) % 1.0f;
            int color = hsvToRgb(hue, 0.6f, 1.0f);

            // Background rect for list item
            Gui.drawRect(drawX - 2, y, screenW + slideX, y + fontH, 0x77000000);
            
            // Right accent line
            Gui.drawRect(screenW - 2 + slideX, y, screenW + slideX, y + fontH, color);

            mc.fontRendererObj.drawString(name, drawX, y + 1, color);
            y += fontH;
        }
    }

    private double calculateBPS() {
        double dx = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double dz = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        return Math.sqrt(dx * dx + dz * dz) * 20.0; // 20 ticks per second
    }

    private int getPing() {
        try {
            NetHandlerPlayClient nh = mc.getNetHandler();
            if (nh == null || mc.thePlayer == null) return 0;
            net.minecraft.client.network.NetworkPlayerInfo info = nh.getPlayerInfo(mc.thePlayer.getUniqueID());
            return info != null ? info.getResponseTime() : 0;
        } catch (Exception e) {
            return 0;
        }
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
        return argb(255, (int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
