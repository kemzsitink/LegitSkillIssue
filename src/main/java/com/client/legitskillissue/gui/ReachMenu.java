package com.client.legitskillissue.gui;

import com.client.legitskillissue.gui.component.*;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ReachMenu — Component-based GUI.
 *
 * Kiến trúc:
 *   Window (root)
 *   ├── UIBlock  (backdrop mờ)
 *   ├── UIBlock  (main frame)  ← scale animation khi mở
 *   │   ├── UIBlock  (header)
 *   │   ├── UIText   (title)
 *   │   ├── UIBlock  (sidebar)
 *   │   │   └── UIBlock × N  (tab buttons, hover effect)
 *   │   └── content area  (scissor clipped, scroll)
 *   │       └── per-module: UIBlock + UIText + UIToggle + UISlider...
 *   └── (keybind overlay khi đang binding)
 */
public class ReachMenu extends GuiScreen {

    // ── Theme ──────────────────────────────────────────────────────────────
    private static final Color C_BG        = new Color(0x0D, 0x0D, 0x11, 0xEE);
    private static final Color C_SIDEBAR   = new Color(0x11, 0x11, 0x16, 0xEE);
    private static final Color C_ACCENT    = new Color(0x4F, 0xAC, 0xEE, 0xFF);
    private static final Color C_TEXT      = new Color(0xF0, 0xF0, 0xF5, 0xFF);
    private static final Color C_DIM       = new Color(0x82, 0x82, 0x8C, 0xFF);
    private static final Color C_BORDER    = new Color(0xFF, 0xFF, 0xFF, 0x1F);
    private static final Color C_HOVER     = new Color(0xFF, 0xFF, 0xFF, 0x18);
    private static final Color C_SEL       = new Color(0x4F, 0xAC, 0xEE, 0x22);
    private static final Color C_MODULE_BG = new Color(0xFF, 0xFF, 0xFF, 0x15);
    private static final Color C_MODULE_HV = new Color(0xFF, 0xFF, 0xFF, 0x25);

    // ── Layout ─────────────────────────────────────────────────────────────
    private static final int W  = 480, H  = 340;
    private static final int SW = 120, HH = 30;   // sidebar width, header height

    // ── State ──────────────────────────────────────────────────────────────
    private int ox, oy;                            // origin (top-left của frame)
    private int activeTab = 0;
    private float scrollY = 0f, targetScrollY = 0f;
    private Module bindingModule = null;

    // ── Open animation ─────────────────────────────────────────────────────
    private float openAnim = 0f;                   // 0→1 khi mở

    // ── Component tree ─────────────────────────────────────────────────────
    private final Window window = new Window();

    // Cached per-frame hit areas cho mouse events (rebuilt mỗi drawScreen)
    private final List<TabHit>    tabHits    = new ArrayList<>();
    private final List<ModuleHit> moduleHits = new ArrayList<>();

    // ── Inner hit-test records ──────────────────────────────────────────────
    private static class TabHit {
        int idx, x, y, w, h;
        TabHit(int i, int x, int y, int w, int h) { idx=i; this.x=x; this.y=y; this.w=w; this.h=h; }
        boolean hit(int mx, int my) { return mx>=x && mx<=x+w && my>=y && my<=y+h; }
    }
    private static class ModuleHit {
        Module module; int x, y, w, h;
        ModuleHit(Module m, int x, int y, int w, int h) { module=m; this.x=x; this.y=y; this.w=w; this.h=h; }
        boolean hit(int mx, int my) { return mx>=x && mx<=x+w && my>=y && my<=y+h; }
    }

    // ── GuiScreen lifecycle ────────────────────────────────────────────────
    @Override
    public void initGui() {
        ox = (width  - W) / 2;
        oy = (height - H) / 2;
        openAnim      = 0f;
        scrollY       = 0f;
        targetScrollY = 0f;
        bindingModule = null;
        window.clear();
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        // ── 1. Tick open animation ──────────────────────────────────────────
        openAnim  = Math.min(1f, openAnim + 0.12f);
        scrollY   = lerp(scrollY, targetScrollY, 0.2f);

        // ── 2. Rebuild component tree mỗi frame (stateless IMGUI style) ────
        window.clear();
        tabHits.clear();
        moduleHits.clear();

        // Backdrop
        window.addChild(new UIBlock(0, 0, width, height, new Color(0, 0, 0, 0x88)));

        // Scale animation: scale từ 0.92 → 1.0 khi mở
        float scale = 0.92f + 0.08f * easeOut(openAnim);
        float alpha = openAnim;

        GlStateManager.pushMatrix();
        GlStateManager.translate(ox + W / 2f, oy + H / 2f, 0);
        GlStateManager.scale(scale, scale, 1f);
        GlStateManager.translate(-(ox + W / 2f), -(oy + H / 2f), 0);

        // ── 3. Main frame ───────────────────────────────────────────────────
        UIBlock frame = new UIBlock(ox, oy, W, H, C_BG);
        frame.setBorderColor(C_BORDER);
        frame.fadeIn();
        frame.tick(1f);
        frame.draw(mx, my);

        // Header bar
        drawRect(ox, oy, ox + W, oy + HH, toArgb(C_SIDEBAR, alpha));
        drawRect(ox, oy + HH, ox + W, oy + HH + 1, toArgb(C_BORDER, alpha));
        fontRendererObj.drawString("LEGITSKILLISSUE", ox + 10, oy + 11, toArgb(C_ACCENT, alpha));

        // ── 4. Sidebar ──────────────────────────────────────────────────────
        drawRect(ox, oy + HH + 1, ox + SW, oy + H, toArgb(C_SIDEBAR, alpha));
        drawRect(ox + SW, oy + HH + 1, ox + SW + 1, oy + H, toArgb(C_BORDER, alpha));

        Category[] cats = Category.values();
        int ty = oy + HH + 12;
        for (int i = 0; i < cats.length; i++) {
            boolean sel = (activeTab == i);
            boolean hov = hit(ox + 5, ty, SW - 10, 24, mx, my);

            if (sel) {
                drawRect(ox + 5, ty, ox + SW - 5, ty + 24, toArgb(C_SEL, alpha));
                drawRect(ox + 5, ty + 4, ox + 7, ty + 20, toArgb(C_ACCENT, alpha));
            } else if (hov) {
                drawRect(ox + 5, ty, ox + SW - 5, ty + 24, toArgb(C_HOVER, alpha));
            }

            fontRendererObj.drawString(cats[i].getName(), ox + 14, ty + 8,
                toArgb(sel ? C_TEXT : C_DIM, alpha));

            tabHits.add(new TabHit(i, ox + 5, ty, SW - 10, 24));
            ty += 28;
        }

        // ── 5. Content area (scissored) ─────────────────────────────────────
        int cx = ox + SW + 12;
        int cw = W - SW - 24;
        int contentTop    = oy + HH + 1;
        int contentBottom = oy + H;

        enableScissor(ox + SW + 1, contentTop, W - SW - 1, H - HH - 1);

        int itemY = (int)(contentTop + 8 - scrollY);

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.getCategory() != cats[activeTab]) continue;

            boolean hov = hit(cx, itemY, cw, 44, mx, my);
            drawRect(cx, itemY, cx + cw, itemY + 44, toArgb(hov ? C_MODULE_HV : C_MODULE_BG, alpha));

            // Accent bar khi enabled
            if (m.isEnabled()) {
                drawRect(cx, itemY, cx + 2, itemY + 44, toArgb(C_ACCENT, alpha));
            }

            // Module name
            fontRendererObj.drawString(m.getName(), cx + 10, itemY + 9, toArgb(C_TEXT, alpha));

            // Status
            boolean en = m.isEnabled();
            fontRendererObj.drawString(en ? "ACTIVE" : "IDLE", cx + 10, itemY + 21,
                toArgb(en ? new Color(0x55, 0xFF, 0x55) : new Color(0xFF, 0x55, 0x55), alpha));

            // Keybind label
            String kb = (m == bindingModule) ? "[ ... ]" : "[ " + m.getKeybindName() + " ]";
            int kbW = fontRendererObj.getStringWidth(kb);
            fontRendererObj.drawString(kb, cx + cw - kbW - 36, itemY + 15, toArgb(C_DIM, alpha));

            // Toggle switch (animé via UIToggle)
            UIToggle toggle = new UIToggle(cx + cw - 32, itemY + 16, m.isEnabled());
            toggle.tick(0.25f);
            toggle.draw(mx, my);

            moduleHits.add(new ModuleHit(m, cx, itemY, cw, 44));
            itemY += 48;

            // ── Settings ────────────────────────────────────────────────────
            for (BooleanSetting s : m.getBooleanSettings()) {
                if (itemY > contentBottom) break;
                drawBoolSetting(cx + 8, itemY, cw - 16, mx, my, s, alpha);
                itemY += 24;
            }
            for (ModeSetting s : m.getModeSettings()) {
                if (itemY > contentBottom) break;
                drawModeSetting(cx + 8, itemY, cw - 16, mx, my, s, alpha);
                itemY += 28;
            }
            for (NumberSetting s : m.getSettings()) {
                if (itemY > contentBottom) break;
                UISlider slider = new UISlider(cx + 8, itemY, cw - 16, s);
                slider.tick(0.2f);
                slider.draw(mx, my);
                itemY += 34;
            }

            itemY += 6; // gap giữa các module
        }

        disableScissor();
        GlStateManager.popMatrix();

        // ── 6. Keybind overlay ──────────────────────────────────────────────
        if (bindingModule != null) {
            String msg = "Press a key to bind  [ ESC = clear ]";
            int mw = fontRendererObj.getStringWidth(msg) + 20;
            int mx2 = (width - mw) / 2, my2 = height / 2 - 10;
            drawRect(mx2, my2, mx2 + mw, my2 + 20, 0xDD111116);
            drawRect(mx2, my2, mx2 + mw, my2 + 1, toArgb(C_ACCENT, 1f));
            fontRendererObj.drawString(msg, mx2 + 10, my2 + 6, toArgb(C_TEXT, 1f));
        }

        window.tick(mx, my);
    }

    // ── Setting renderers ──────────────────────────────────────────────────
    private void drawBoolSetting(int x, int y, int w, int mx, int my, BooleanSetting s, float alpha) {
        fontRendererObj.drawString(s.getName(), x, y + 5, toArgb(C_DIM, alpha));
        UIToggle t = new UIToggle(x + w - 28, y + 2, s.getValue());
        t.tick(0.25f);
        t.draw(mx, my);
    }

    private void drawModeSetting(int x, int y, int w, int mx, int my, ModeSetting s, float alpha) {
        fontRendererObj.drawString(s.getName(), x, y + 5, toArgb(C_DIM, alpha));
        String val = s.getMode();
        int vw = fontRendererObj.getStringWidth(val) + 10;
        boolean hov = hit(x + w - vw, y, vw, 18, mx, my);
        drawRect(x + w - vw, y, x + w, y + 18, toArgb(hov ? C_HOVER : new Color(0xFF,0xFF,0xFF,0x22), alpha));
        fontRendererObj.drawString(val, x + w - vw + 5, y + 5, toArgb(C_ACCENT, alpha));
    }

    // ── Mouse & Keyboard ───────────────────────────────────────────────────
    @Override
    protected void mouseClicked(int mx, int my, int btn) throws IOException {
        if (bindingModule != null) { bindingModule = null; return; }

        // Tab clicks
        for (TabHit t : tabHits) {
            if (t.hit(mx, my)) { activeTab = t.idx; targetScrollY = 0; return; }
        }

        // Module clicks
        for (ModuleHit mh : moduleHits) {
            if (mh.hit(mx, my)) {
                if (btn == 0) mh.module.toggle();
                else if (btn == 1) bindingModule = mh.module;
                return;
            }
        }

        // Boolean / Mode setting clicks — re-walk modules
        Category[] cats = Category.values();
        int cx = ox + SW + 12, cw = W - SW - 24;
        int itemY = (int)(oy + HH + 9 - scrollY);

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.getCategory() != cats[activeTab]) continue;
            itemY += 48;
            for (BooleanSetting s : m.getBooleanSettings()) {
                if (hit(cx + 8, itemY, cw - 16, 24, mx, my)) { s.toggle(); return; }
                itemY += 24;
            }
            for (ModeSetting s : m.getModeSettings()) {
                if (hit(cx + 8, itemY, cw - 16, 28, mx, my)) { s.cycle(); return; }
                itemY += 28;
            }
            for (NumberSetting ignored : m.getSettings()) { itemY += 34; }
            itemY += 6;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dw = Mouse.getEventDWheel();
        if (dw != 0) targetScrollY = Math.max(0, targetScrollY - (dw > 0 ? 30 : -30));
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (bindingModule != null) {
            bindingModule.setKeybind(key == Keyboard.KEY_ESCAPE ? Keyboard.KEY_NONE : key);
            bindingModule = null;
            return;
        }
        if (key == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    // ── Helpers ────────────────────────────────────────────────────────────
    private static boolean hit(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static int toArgb(Color c, float alpha) {
        int a = Math.min(255, (int)(c.getAlpha() * alpha));
        return (a << 24) | (c.getRGB() & 0x00FFFFFF);
    }

    private static float lerp(float a, float b, float t) {
        float d = b - a;
        return Math.abs(d) < 0.5f ? b : a + d * t;
    }

    /** Ease-out cubic cho open animation */
    private static float easeOut(float t) {
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }

    private void enableScissor(int x, int y, int w, int h) {
        int f = new net.minecraft.client.gui.ScaledResolution(mc).getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * f, (mc.displayHeight - (y + h) * f), w * f, h * f);
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
