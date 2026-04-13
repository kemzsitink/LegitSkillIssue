package com.example.ablabla.gui;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.utils.TpsTracker;
import net.minecraft.client.Minecraft;
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
 * HudRenderer — Arraylist with:
 *   - Chroma gradient (hue shifts down the list)
 *   - Sort by text width (longest first → staircase shape)
 *   - Slide-in / slide-out animation per module
 *   - Watermark (top-left): client name | TPS | FPS | Ping
 */
public class HudRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();

    // Per-module slide offset (0 = fully on screen, 1 = fully off right edge)
    private final Map<String, Float> slideOffsets = new HashMap<>();

    // Chroma base hue — advances each frame for rainbow effect
    private float chromaHue = 0f;

    // ── Render ─────────────────────────────────────────────────────

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.thePlayer == null || mc.gameSettings.showDebugInfo) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int screenW = sr.getScaledWidth();

        // Advance chroma hue — full cycle every ~10 seconds at 60fps
        chromaHue = (chromaHue + 0.0017f) % 1.0f;

        // ── Watermark (top-left) ──────────────────────────────────
        drawWatermark(sr);

        // ── Arraylist (top-right) ─────────────────────────────────
        List<Module> allModules = new ArrayList<>(ModuleManager.INSTANCE.getModules());

        // Sort by display width descending — longest name at top
        allModules.sort(Comparator.comparingInt(
                m -> -mc.fontRendererObj.getStringWidth(m.getName())));

        int y = 2;
        int fontH = mc.fontRendererObj.FONT_HEIGHT + 1;

        for (int i = 0; i < allModules.size(); i++) {
            Module m = allModules.get(i);
            String name = m.getName();

            // Animate slide offset
            float current = slideOffsets.getOrDefault(name, 1.0f);
            float target  = m.isEnabled() ? 0.0f : 1.0f;
            // Lerp speed: 0.15 per frame (~8 frames to fully slide in at 60fps)
            float next = current + (target - current) * 0.15f;
            // Snap to 0 or 1 when close enough to avoid perpetual micro-updates
            if (Math.abs(next - target) < 0.005f) next = target;
            slideOffsets.put(name, next);

            // Skip if fully off-screen
            if (next >= 0.999f) continue;

            int textW  = mc.fontRendererObj.getStringWidth(name);
            int slideX = (int)(textW * next); // pixels shifted right (off-screen)
            int drawX  = screenW - textW - 2 + slideX;

            // Chroma color — hue offset per row
            float hue   = (chromaHue + i * 0.06f) % 1.0f;
            int   color = hsvToRgb(hue, 0.55f, 1.0f);
            int   shadow = argb(180, 0, 0, 0);

            // Shadow
            mc.fontRendererObj.drawString(name, drawX + 1, y + 1, shadow);
            // Text
            mc.fontRendererObj.drawString(name, drawX, y, color);

            y += fontH;
        }
    }

    // ── Watermark ──────────────────────────────────────────────────

    private void drawWatermark(ScaledResolution sr) {
        int fps  = Minecraft.getDebugFPS();
        int ping = getPing();
        float tps = TpsTracker.INSTANCE.getTps();

        String tpsStr = String.format("%.1f", tps);
        String text = "LegitSkillIssue  |  TPS: " + tpsStr + "  |  FPS: " + fps + "  |  Ping: " + ping + "ms";

        // Chroma color for watermark
        int color = hsvToRgb(chromaHue, 0.5f, 1.0f);

        mc.fontRendererObj.drawString(text, 3, 3, argb(180, 0, 0, 0)); // shadow
        mc.fontRendererObj.drawString(text, 2, 2, color);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private int getPing() {
        try {
            NetHandlerPlayClient nh = mc.getNetHandler();
            if (nh == null || mc.thePlayer == null) return 0;
            net.minecraft.client.network.NetworkPlayerInfo info =
                    nh.getPlayerInfo(mc.thePlayer.getUniqueID());
            return info != null ? info.getResponseTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * HSV → packed ARGB (alpha = 255).
     * h in [0,1], s in [0,1], v in [0,1].
     */
    private static int hsvToRgb(float h, float s, float v) {
        int   hi = (int)(h * 6) % 6;
        float f  = h * 6 - (int)(h * 6);
        float p  = v * (1 - s);
        float q  = v * (1 - f * s);
        float t  = v * (1 - (1 - f) * s);
        float r, g, b;
        switch (hi) {
            case 0:  r = v; g = t; b = p; break;
            case 1:  r = q; g = v; b = p; break;
            case 2:  r = p; g = v; b = t; break;
            case 3:  r = p; g = q; b = v; break;
            case 4:  r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return argb(255, (int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
