package com.example.ablabla.gui;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.module.impl.AimAssistMod;
import com.example.ablabla.module.impl.AutoClickerMod;
import com.example.ablabla.module.impl.BacktrackMod;
import com.example.ablabla.module.impl.HitBoxMod;
import com.example.ablabla.module.impl.KillAuraMod;
import com.example.ablabla.module.impl.ReachMod;
import com.example.ablabla.module.impl.VelocityMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public class ReachMenu extends GuiScreen {

    // ── Fluent UI Palette ──────────────────────────────────────────
    private static final int BG          = argb(245, 13,  13,  17 );  // near-black, slight blue tint
    private static final int BG_SIDEBAR  = argb(245, 17,  17,  22 );
    private static final int BG_CARD     = argb(255, 24,  24,  30 );
    private static final int BG_CARD_HOV = argb(255, 32,  32,  40 );
    private static final int ACCENT      = argb(255, 79,  172, 238);  // Fluent blue
    private static final int ACCENT_DIM  = argb(80,  79,  172, 238);
    private static final int TEXT        = argb(255, 240, 240, 245);
    private static final int TEXT_DIM    = argb(255, 130, 130, 140);
    private static final int DIVIDER     = argb(30,  255, 255, 255);
    private static final int SWITCH_OFF  = argb(255, 50,  50,  58 );
    private static final int SHADOW      = argb(120, 0,   0,   0  );

    // ── Window ─────────────────────────────────────────────────────
    private static final int W  = 440;
    private static final int H  = 320;
    private static final int SB = 115;   // sidebar width
    private static final int TB = 24;    // title bar height
    private static final int R  = 6;     // corner radius
    private static final int PAD = 12;

    // ── Card ───────────────────────────────────────────────────────
    private static final int CARD_H   = 48;
    private static final int CARD_GAP = 6;
    private static final int CARD_R   = 4;

    // ── State ──────────────────────────────────────────────────────
    private int winX, winY;
    private boolean dragging;
    private int dragDX, dragDY;
    private int activeTab = 0;
    private int scrollY   = 0;
    private int sliderDrag = -1;

    // ── Refs ───────────────────────────────────────────────────────
    private final AimAssistMod  aimAssist  = ModuleManager.INSTANCE.getModule(AimAssistMod.class);
    private final ReachMod      reach      = ModuleManager.INSTANCE.getModule(ReachMod.class);
    private final HitBoxMod     hitBox     = ModuleManager.INSTANCE.getModule(HitBoxMod.class);
    private final VelocityMod   velocity   = ModuleManager.INSTANCE.getModule(VelocityMod.class);
    private final BacktrackMod  backtrack  = ModuleManager.INSTANCE.getModule(BacktrackMod.class);
    private final AutoClickerMod autoClick = ModuleManager.INSTANCE.getModule(AutoClickerMod.class);

    // ── Data ───────────────────────────────────────────────────────
    private static final String[] TABS      = {"Combat", "Movement", "Player", "Misc"};
    private static final String[] SUBTITLES = {"Battle modules", "Agility & speed", "Personal", "Utilities"};

    private static final Object[][][] TAB_SLIDERS = {
        { // Combat
            {"Reach",      "Attack distance",     3.0f, 6.0f,  0.1f },
            {"Aim FOV",    "AimAssist FOV",        10f,  180f,  5f   },
            {"Aim Speed",  "Rotation speed x100",  5f,   80f,  1f   },
            {"Min CPS",    "AutoClicker min CPS",  6f,   20f,  1f   },
            {"Max CPS",    "AutoClicker max CPS",  6f,   30f,  1f   },
            {"H-Velocity", "KB% (0=none, 100=full)", 0f,   100f, 5f   },
            {"V-Velocity", "Vert KB% (0=none)",      0f,   100f, 5f   },
            {"BT Delay",   "Backtrack delay ms",   0f,   500f, 10f  },
            {"HitBox",     "Hitbox expand size",   0f,   30.0f, 0.05f},
            {"KA Range",   "KillAura range",       2.0f, 6.0f,  0.1f },
            {"KA Speed",   "KillAura rot speed",   10f,  100f,  5f   },
        },
        {}, // Movement
        {}, // Player
        {}, // Misc
    };

    private static final String[][] TAB_MODULES = {
        {"WTap","AutoClicker","Velocity","BlockHit","Backtrack","AimAssist","HitBox","Reach","PlayerESP","KillAura"},
        {"Sprint","NoSlow","Eagle"},
        {"FastPlace"},
        {"WTap","Sprint","NoSlow","Eagle","FastPlace","AutoClicker","Velocity","BlockHit","Backtrack","AimAssist","HitBox","Reach"},
    };

    // ── Init ───────────────────────────────────────────────────────

    @Override
    public void initGui() {
        winX = (width  - W) / 2;
        winY = (height - H) / 2;
        scrollY = 0;
    }

    // ── Render ─────────────────────────────────────────────────────

    @Override
    public void drawScreen(int mx, int my, float pt) {
        // backdrop dim
        drawRect(0, 0, width, height, argb(160, 0, 0, 0));

        int wx = winX, wy = winY;

        // drop shadow (layered rects for soft shadow)
        for (int i = 4; i >= 1; i--) {
            drawRect(wx - i, wy - i, wx + W + i, wy + H + i, argb(18 * (5 - i), 0, 0, 0));
        }

        // ── Window background (rounded) ──
        fillRounded(wx, wy, W, H, R, BG);

        // ── Title bar ──
        fillRounded(wx, wy, W, TB + R, R, argb(255, 17, 17, 22)); // slightly lighter
        drawRect(wx, wy + TB, wx + W, wy + TB + 1, DIVIDER);

        // title text
        fontRendererObj.drawString("LegitSkillIssue", wx + 10, wy + 8, TEXT_DIM);

        // close button
        boolean hoverX = mx >= wx + W - 20 && mx <= wx + W - 6 && my >= wy + 5 && my <= wy + TB - 5;
        if (hoverX) fillRounded(wx + W - 22, wy + 4, 16, TB - 8, 3, argb(200, 196, 43, 28));
        fontRendererObj.drawString("x", wx + W - 15, wy + 8, hoverX ? TEXT : TEXT_DIM);

        // ── Sidebar ──
        drawRect(wx, wy + TB + 1, wx + SB, wy + H, BG_SIDEBAR);
        drawRect(wx + SB, wy + TB + 1, wx + SB + 1, wy + H, DIVIDER);

        int navY = wy + TB + 10;
        for (int i = 0; i < TABS.length; i++) {
            boolean active = i == activeTab;
            boolean hov    = !active && mx >= wx + 6 && mx <= wx + SB - 6
                          && my >= navY && my <= navY + 22;

            if (active) {
                fillRounded(wx + 6, navY, SB - 12, 22, 4, ACCENT_DIM);
                // left accent pill
                fillRounded(wx + 6, navY + 4, 3, 14, 2, ACCENT);
            } else if (hov) {
                fillRounded(wx + 6, navY, SB - 12, 22, 4, argb(20, 255, 255, 255));
            }

            fontRendererObj.drawString(TABS[i], wx + 16, navY + 7, active ? TEXT : (hov ? TEXT : TEXT_DIM));
            navY += 28;
        }

        // ── Content area ──
        int cx = wx + SB + 1;
        int cy = wy + TB + 1;
        int cw = W - SB - 1;
        int ch = H - TB - 1;

        // header
        fontRendererObj.drawString(TABS[activeTab], cx + PAD, cy + 8, TEXT);
        fontRendererObj.drawString(SUBTITLES[activeTab], cx + PAD, cy + 19, ACCENT);
        drawRect(cx + PAD, cy + 30, cx + cw - PAD, cy + 31, DIVIDER);

        // list area bounds
        int listX   = cx + PAD;
        int listY   = cy + 34;
        int listW   = cw - PAD * 2;
        int listH   = ch - 36;

        // ── GL Scissor: clip cards to list area ──
        enableScissor(listX, listY, listW, listH);
        try {
            int itemY = listY - scrollY;

            // sliders for current tab
            if (activeTab == 0) {
                Object[][] sliders = TAB_SLIDERS[activeTab];
                for (int i = 0; i < sliders.length; i++) {
                    drawSliderCard(listX, itemY, listW, mx, my, i);
                    itemY += CARD_H + CARD_GAP;
                }
            }

            // module cards
            for (Module m : getTabModules()) {
                drawModuleCard(listX, itemY, listW, mx, my, m);
                itemY += CARD_H + CARD_GAP;
            }
        } finally {
            disableScissor();
        }

        // scrollbar (outside scissor so it's always visible)
        int totalItems = TAB_SLIDERS[activeTab].length + getTabModules().size();
        int totalH = totalItems * (CARD_H + CARD_GAP);
        if (totalH > listH) {
            int sbH   = Math.max(20, listH * listH / totalH);
            int maxSc = totalH - listH;
            int sbY   = listY + (int)((long) scrollY * (listH - sbH) / maxSc);
            drawRect(cx + cw - 4, listY, cx + cw - 2, listY + listH, argb(20, 255, 255, 255));
            fillRounded(cx + cw - 4, sbY, 2, sbH, 1, argb(100, 255, 255, 255));
        }
    }

    // ── Card drawing ───────────────────────────────────────────────

    private void drawSliderCard(int x, int y, int w, int mx, int my, int idx) {
        boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + CARD_H;
        fillRounded(x, y, w, CARD_H, CARD_R, hov ? BG_CARD_HOV : BG_CARD);

        String name = (String) TAB_SLIDERS[activeTab][idx][0];
        String desc = (String) TAB_SLIDERS[activeTab][idx][1];
        float  min  = toF(TAB_SLIDERS[activeTab][idx][2]);
        float  max  = toF(TAB_SLIDERS[activeTab][idx][3]);
        float  step = toF(TAB_SLIDERS[activeTab][idx][4]);
        float  val  = getSliderVal(idx);

        fontRendererObj.drawString(name, x + 10, y + 9,  TEXT);
        fontRendererObj.drawString(desc, x + 10, y + 20, TEXT_DIM);

        // value
        String vs = step >= 1 ? String.valueOf((int) val) : String.format("%.1f", val);
        int vsW = fontRendererObj.getStringWidth(vs);
        fontRendererObj.drawString(vs, x + w - vsW - 10, y + 9, ACCENT);

        // track
        int tx = x + 10, tw = w - 20;
        int ty = y + 36;
        fillRounded(tx, ty - 1, tw, 4, 2, argb(60, 255, 255, 255));
        float pct    = (val - min) / (max - min);
        int   filled = (int)(pct * tw);
        fillRounded(tx, ty - 1, filled, 4, 2, ACCENT);
        // thumb circle
        fillCircle(tx + filled, ty + 1, 5, BG_CARD);
        fillCircle(tx + filled, ty + 1, 4, ACCENT);

        // drag
        if (sliderDrag == idx) {
            float np = Math.max(0, Math.min(1, (float)(mx - tx) / tw));
            setSliderVal(idx, Math.round((min + np * (max - min)) / step) * step);
        }
    }

    private void drawModuleCard(int x, int y, int w, int mx, int my, Module m) {
        boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + CARD_H;
        fillRounded(x, y, w, CARD_H, CARD_R, hov ? BG_CARD_HOV : BG_CARD);

        // left accent bar when enabled
        if (m.isEnabled()) {
            fillRounded(x, y + 8, 3, CARD_H - 16, 2, ACCENT);
        }

        fontRendererObj.drawString(m.getName(), x + 14, y + 11, TEXT);
        fontRendererObj.drawString(m.isEnabled() ? "Enabled" : "Disabled",
            x + 14, y + 23, m.isEnabled() ? ACCENT : TEXT_DIM);

        // Fluent toggle switch
        int sw = 30, sh = 14;
        int sx = x + w - sw - 10;
        int sy = y + CARD_H / 2 - sh / 2;
        fillRounded(sx, sy, sw, sh, sh / 2, m.isEnabled() ? ACCENT : SWITCH_OFF);
        int kx = m.isEnabled() ? sx + sw - sh + 2 : sx + 2;
        fillCircle(kx + (sh - 4) / 2, sy + sh / 2, (sh - 4) / 2, TEXT);
    }

    // ── OpenGL helpers ─────────────────────────────────────────────

    /** Clip rendering to a rectangle using GL scissor */
    private void enableScissor(int x, int y, int w, int h) {
        double scaleX = mc.displayWidth  / (double) width;
        double scaleY = mc.displayHeight / (double) height;
        int sx = (int)(x * scaleX);
        int sy = (int)(mc.displayHeight - (y + h) * scaleY);
        int sw = (int)(w * scaleX);
        int sh = (int)(h * scaleY);
        // clamp to valid values — glScissor throws GL_INVALID_VALUE if w/h < 0
        if (sw <= 0 || sh <= 0) return;
        sx = Math.max(0, sx);
        sy = Math.max(0, sy);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sx, sy, sw, sh);
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        // clear any GL error caused by scissor to prevent Minecraft's post-render error check from logging it
        GL11.glGetError();
    }

    /** Fill a rounded rectangle by drawing a cross + 4 corner circles */
    private void fillRounded(int x, int y, int w, int h, int r, int color) {
        if (r <= 0 || w <= 0 || h <= 0) { drawRect(x, y, x + w, y + h, color); return; }
        r = Math.min(r, Math.min(w, h) / 2);
        // center cross
        drawRect(x + r, y,     x + w - r, y + h,     color);
        drawRect(x,     y + r, x + r,     y + h - r, color);
        drawRect(x + w - r, y + r, x + w, y + h - r, color);
        // 4 corners
        fillCircleQuadrant(x + r,         y + r,         r, color, 2);
        fillCircleQuadrant(x + w - r,     y + r,         r, color, 1);
        fillCircleQuadrant(x + r,         y + h - r,     r, color, 3);
        fillCircleQuadrant(x + w - r,     y + h - r,     r, color, 4);
    }

    private void fillCircle(int cx, int cy, int r, int color) {
        for (int i = 0; i < 4; i++) fillCircleQuadrant(cx, cy, r, color, i + 1);
    }

    /** Draw one quadrant of a circle. q: 1=TR, 2=TL, 3=BL, 4=BR */
    private void fillCircleQuadrant(int cx, int cy, int r, int color, int q) {
        float a = (float)(color >> 24 & 255) / 255f;
        float red = (float)(color >> 16 & 255) / 255f;
        float g = (float)(color >> 8  & 255) / 255f;
        float b = (float)(color & 255) / 255f;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, g, b, a);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        wr.pos(cx, cy, 0).endVertex();

        int startDeg = (q - 1) * 90;
        for (int deg = startDeg; deg <= startDeg + 90; deg += 5) {
            double rad = Math.toRadians(deg);
            double px, py;
            switch (q) {
                case 1: px = cx + Math.cos(rad) * r; py = cy - Math.sin(rad) * r; break;
                case 2: px = cx - Math.sin(rad) * r; py = cy - Math.cos(rad) * r; break;
                case 3: px = cx - Math.cos(rad) * r; py = cy + Math.sin(rad) * r; break;
                default:px = cx + Math.sin(rad) * r; py = cy + Math.cos(rad) * r; break;
            }
            wr.pos(px, py, 0).endVertex();
        }
        tess.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    // ── Input ──────────────────────────────────────────────────────

    @Override
    protected void mouseClicked(int mx, int my, int btn) throws IOException {
        if (btn != 0) return;

        // close
        if (mx >= winX + W - 20 && mx <= winX + W - 6 && my >= winY + 5 && my <= winY + TB - 5) {
            mc.displayGuiScreen(null); return;
        }
        // drag
        if (my >= winY && my <= winY + TB && mx >= winX && mx <= winX + W - 24) {
            dragging = true; dragDX = mx - winX; dragDY = my - winY; return;
        }
        // tabs
        int navY = winY + TB + 10;
        for (int i = 0; i < TABS.length; i++) {
            if (mx >= winX + 6 && mx <= winX + SB - 6 && my >= navY && my <= navY + 22) {
                activeTab = i; scrollY = 0; return;
            }
            navY += 28;
        }

        // content
        int cx = winX + SB + 1 + PAD;
        int cy = winY + TB + 1 + 34;
        int cw = W - SB - 1 - PAD * 2;
        int itemY = cy - scrollY;

        if (activeTab == 0) {
            Object[][] sliders = TAB_SLIDERS[activeTab];
            for (int i = 0; i < sliders.length; i++) {
                if (my >= itemY && my <= itemY + CARD_H) {
                    int tx = cx, tw = cw;
                    if (mx >= tx && mx <= tx + tw) { sliderDrag = i; return; }
                }
                itemY += CARD_H + CARD_GAP;
            }
        }
        for (Module m : getTabModules()) {
            if (my >= itemY && my <= itemY + CARD_H && mx >= cx && mx <= cx + cw) {
                m.toggle(); return;
            }
            itemY += CARD_H + CARD_GAP;
        }
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        dragging = false; sliderDrag = -1;
    }

    @Override
    protected void mouseClickMove(int mx, int my, int btn, long time) {
        if (dragging) { winX = mx - dragDX; winY = my - dragDY; }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dw = Mouse.getEventDWheel();
        if (dw != 0) {
            scrollY = Math.max(0, scrollY - (dw > 0 ? 12 : -12));
        }
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (key == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    // ── Helpers ────────────────────────────────────────────────────

    private List<Module> getTabModules() {
        Set<String> names = new HashSet<>(Arrays.asList(TAB_MODULES[activeTab]));
        List<Module> result = new ArrayList<>();
        for (Module m : ModuleManager.INSTANCE.getModules())
            if (names.contains(m.getName())) result.add(m);
        return result;
    }

    private float getSliderVal(int idx) {
        switch (idx) {
            case 0: return reach     != null ? reach.reachDistance       : 3.5f;
            case 1: return aimAssist != null ? aimAssist.fov             : 45f;
            case 2: return aimAssist != null ? aimAssist.speed            : 15f;
            case 3: return autoClick != null ? (float) autoClick.minCps  : 10f;
            case 4: return autoClick != null ? (float) autoClick.maxCps  : 14f;
            case 5: return velocity  != null ? velocity.hPercent         : 0f;
            case 6: return velocity  != null ? velocity.vPercent         : 0f;
            case 7: return backtrack != null ? backtrack.delayMs         : 150f;
            case 8: return hitBox    != null ? hitBox.expand             : 0.2f;
            case 9: { KillAuraMod ka = ModuleManager.INSTANCE.getModule(KillAuraMod.class); return ka != null ? ka.range : 4.0f; }
            case 10: { KillAuraMod ka = ModuleManager.INSTANCE.getModule(KillAuraMod.class); return ka != null ? ka.rotSpeed * 100f : 80f; }
        }
        return 0;
    }

    private void setSliderVal(int idx, float v) {
        switch (idx) {
            case 0: if (reach     != null) reach.reachDistance      = v;          break;
            case 1: if (aimAssist != null) aimAssist.fov            = v;          break;
            case 2: if (aimAssist != null) aimAssist.speed          = v;          break;
            case 3: if (autoClick != null) autoClick.minCps         = v;          break;
            case 4: if (autoClick != null) autoClick.maxCps         = v;          break;
            case 5: if (velocity  != null) velocity.hPercent        = (int) v;    break;
            case 6: if (velocity  != null) velocity.vPercent        = (int) v;    break;
            case 7: if (backtrack != null) backtrack.delayMs        = (int) v;    break;
            case 8: if (hitBox    != null) hitBox.expand            = v;          break;
            case 9: { KillAuraMod ka = ModuleManager.INSTANCE.getModule(KillAuraMod.class); if (ka != null) ka.range = v; break; }
            case 10: { KillAuraMod ka = ModuleManager.INSTANCE.getModule(KillAuraMod.class); if (ka != null) ka.rotSpeed = v / 100f; break; }
        }
    }

    private static float toF(Object o) {
        if (o instanceof Float)   return (Float)   o;
        if (o instanceof Integer) return (Integer) o;
        return 0f;
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
