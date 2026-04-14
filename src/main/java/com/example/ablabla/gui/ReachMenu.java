package com.example.ablabla.gui;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.module.setting.ModeSetting;
import com.example.ablabla.module.setting.NumberSetting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

/**
 * ARCHITECT IMGUI - Performance-first, Data-driven UI.
 * Redesigned to feel like professional tools (ImGui inspired).
 */
public class ReachMenu extends GuiScreen {

    // Theme Palette
    private final int CLR_BG         = 0xEE0D0D11;
    private final int CLR_SIDEBAR    = 0xEE111116;
    private final int CLR_ACCENT     = 0xFF4FACEE;
    private final int CLR_TEXT       = 0xFFF0F0F5;
    private final int CLR_TEXT_DIM   = 0xFF82828C;
    private final int CLR_BORDER     = 0x1FFFFFFF;

    // Layout Constants
    private final int WIDTH = 480, HEIGHT = 340;
    private final int SIDEBAR_WIDTH = 120;
    private final int HEADER_HEIGHT = 30;
    
    private int x, y;
    private int activeTab = 0;
    private final String[] tabs = {"Combat", "Movement", "Player", "Render"};

    // Animation & State
    private float openAnimation = 0f;
    private float scrollY = 0f;
    private Module bindingModule = null;

    @Override
    public void initGui() {
        this.x = (width - WIDTH) / 2;
        this.y = (height - HEIGHT) / 2;
        this.openAnimation = 0f;
        this.bindingModule = null;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        // Simple entrance animation
        openAnimation = Math.min(1.0f, openAnimation + 0.15f);
        
        // Background dim
        drawRect(0, 0, width, height, 0x88000000);

        GlStateManager.pushMatrix();
        float scale = 0.95f + (0.05f * openAnimation);
        GlStateManager.translate(x + WIDTH / 2f, y + HEIGHT / 2f, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-(x + WIDTH / 2f), -(y + HEIGHT / 2f), 0);

        // Main Frame
        drawRect(x, y, x + WIDTH, y + HEIGHT, CLR_BG);
        drawRect(x, y, x + WIDTH, y + HEADER_HEIGHT, CLR_SIDEBAR);
        drawRect(x, y + HEADER_HEIGHT, x + WIDTH, y + HEADER_HEIGHT + 1, CLR_BORDER);
        
        fontRendererObj.drawString("ARCHITECT :: SYSTEM_DASHBOARD", x + 10, y + 11, CLR_ACCENT);

        // Sidebar
        drawRect(x, y + HEADER_HEIGHT + 1, x + SIDEBAR_WIDTH, y + HEIGHT, CLR_SIDEBAR);
        drawRect(x + SIDEBAR_WIDTH, y + HEADER_HEIGHT + 1, x + SIDEBAR_WIDTH + 1, y + HEIGHT, CLR_BORDER);

        renderTabs(mx, my);
        renderContent(mx, my);

        GlStateManager.popMatrix();
    }

    private void renderTabs(int mx, int my) {
        int ty = y + HEADER_HEIGHT + 15;
        for (int i = 0; i < tabs.length; i++) {
            boolean hovered = isHovered(x + 5, ty, SIDEBAR_WIDTH - 10, 25, mx, my);
            boolean selected = (activeTab == i);

            if (selected) {
                drawRect(x + 5, ty, x + SIDEBAR_WIDTH - 5, ty + 25, 0x224FACEE);
                drawRect(x + 5, ty + 5, x + 7, ty + 20, CLR_ACCENT);
            } else if (hovered) {
                drawRect(x + 5, ty, x + SIDEBAR_WIDTH - 5, ty + 25, 0x11FFFFFF);
            }

            fontRendererObj.drawString(tabs[i], x + 15, ty + 9, selected ? CLR_TEXT : CLR_TEXT_DIM);
            ty += 30;
        }
    }

    private void renderContent(int mx, int my) {
        int cx = x + SIDEBAR_WIDTH + 15;
        int cy = y + HEADER_HEIGHT + 15;
        int cw = WIDTH - SIDEBAR_WIDTH - 30;
        
        enableScissor(x + SIDEBAR_WIDTH + 1, y + HEADER_HEIGHT + 1, WIDTH - SIDEBAR_WIDTH - 1, HEIGHT - HEADER_HEIGHT - 1);
        
        int itemY = (int) (cy - scrollY);
        List<Module> modules = ModuleManager.INSTANCE.getModules();

        for (Module m : modules) {
            if (!getCategory(m).equalsIgnoreCase(tabs[activeTab])) continue;

            // Module Header
            boolean hov = isHovered(cx, itemY, cw, 45, mx, my);
            drawRect(cx, itemY, cx + cw, itemY + 45, hov ? 0x25FFFFFF : 0x15FFFFFF);
            if (m.isEnabled()) drawRect(cx, itemY, cx + 2, itemY + 45, CLR_ACCENT);
            
            fontRendererObj.drawString(m.getName(), cx + 10, itemY + 10, CLR_TEXT);
            String status = m.isEnabled() ? "RUNNING" : "STOPPED";
            fontRendererObj.drawString(status, cx + 10, itemY + 22, m.isEnabled() ? 0xFF55FF55 : 0xFFFF5555);
            
            // Hotkey Label (Right-click to bind)
            String keyName = (m == bindingModule) ? "..." : "[" + m.getKeybindName() + "]";
            int kw = fontRendererObj.getStringWidth(keyName);
            fontRendererObj.drawString(keyName, cx + cw - kw - 45, itemY + 12, CLR_TEXT_DIM);

            // Toggle Switch
            int sw = 30, sh = 12;
            int sx = cx + cw - sw - 10;
            int sy = itemY + 10;
            drawRect(sx, sy, sx + sw, sy + sh, m.isEnabled() ? CLR_ACCENT : 0xFF333338);
            drawRect(m.isEnabled() ? sx + sw - 8 : sx, sy - 2, m.isEnabled() ? sx + sw : sx + 8, sy + sh + 2, 0xFFFFFFFF);

            itemY += 50;

            for (ModeSetting s : m.getModeSettings()) {
                renderModeSetting(cx + 10, itemY, cw - 20, mx, my, s);
                itemY += 30;
            }
            for (NumberSetting s : m.getSettings()) {
                renderNumberSetting(cx + 10, itemY, cw - 20, mx, my, s);
                itemY += 35;
            }
            
            itemY += 10;
        }
        
        disableScissor();
    }

    private void renderModeSetting(int x, int y, int w, int mx, int my, ModeSetting s) {
        fontRendererObj.drawString(s.getName(), x, y + 5, CLR_TEXT_DIM);
        String val = s.getMode();
        int vw = fontRendererObj.getStringWidth(val);
        boolean hov = isHovered(x + w - vw - 10, y, vw + 10, 18, mx, my);
        drawRect(x + w - vw - 10, y, x + w, y + 18, hov ? 0x44FFFFFF : 0x22FFFFFF);
        fontRendererObj.drawString(val, x + w - vw - 5, y + 5, CLR_ACCENT);
    }

    private void renderNumberSetting(int x, int y, int w, int mx, int my, NumberSetting s) {
        fontRendererObj.drawString(s.getName(), x, y, CLR_TEXT_DIM);
        String val = String.valueOf(s.getValue());
        fontRendererObj.drawString(val, x + w - fontRendererObj.getStringWidth(val), y, CLR_ACCENT);
        
        int sy = y + 12;
        int sw = w;
        drawRect(x, sy, x + sw, sy + 2, 0x33FFFFFF);
        float pct = (s.getValue() - s.getMin()) / (s.getMax() - s.getMin());
        drawRect(x, sy, x + (int)(sw * pct), sy + 2, CLR_ACCENT);
        drawRect(x + (int)(sw * pct) - 2, sy - 3, x + (int)(sw * pct) + 2, sy + 5, 0xFFFFFFFF);

        if (Mouse.isButtonDown(0) && isHovered(x, sy - 5, sw, 12, mx, my)) {
            float newVal = s.getMin() + ((float)(mx - x) / sw) * (s.getMax() - s.getMin());
            s.setValue(newVal);
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) throws IOException {
        if (bindingModule != null) {
            bindingModule = null; // Cancel binding if clicking elsewhere
            return;
        }

        int ty = y + HEADER_HEIGHT + 15;
        for (int i = 0; i < tabs.length; i++) {
            if (isHovered(x + 5, ty, SIDEBAR_WIDTH - 10, 25, mx, my)) {
                activeTab = i;
                scrollY = 0;
                return;
            }
            ty += 30;
        }

        int cx = x + SIDEBAR_WIDTH + 15;
        int cy = y + HEADER_HEIGHT + 15;
        int cw = WIDTH - SIDEBAR_WIDTH - 30;
        int itemY = (int) (cy - scrollY);

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (!getCategory(m).equalsIgnoreCase(tabs[activeTab])) continue;
            
            if (isHovered(cx, itemY, cw, 45, mx, my)) {
                if (btn == 0) {
                    m.toggle();
                } else if (btn == 1) {
                    bindingModule = m; // Enter binding mode on right click
                }
                return;
            }
            itemY += 50;
            
            for (ModeSetting s : m.getModeSettings()) {
                if (isHovered(cx + 10, itemY, cw - 20, 30, mx, my)) {
                    s.cycle();
                    return;
                }
                itemY += 30;
            }
            for (NumberSetting s : m.getSettings()) {
                itemY += 35;
            }
            itemY += 10;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dw = Mouse.getEventDWheel();
        if (dw != 0) {
            scrollY = Math.max(0, scrollY - (dw > 0 ? 25 : -25));
        }
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (bindingModule != null) {
            if (key == Keyboard.KEY_ESCAPE) {
                bindingModule.setKeybind(Keyboard.KEY_NONE);
            } else {
                bindingModule.setKeybind(key);
            }
            bindingModule = null;
            return;
        }
        if (key == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(null);
    }

    private boolean isHovered(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private String getCategory(Module m) {
        if (m.getName().contains("ESP") || m.getName().contains("Fullbright") || m.getName().contains("Chams")) return "Render";
        if (m.getName().contains("Sprint") || m.getName().contains("NoSlow") || m.getName().contains("Flight") || m.getName().contains("Jump")) return "Movement";
        if (m.getName().contains("Stress") || m.getName().contains("Chest") || m.getName().contains("Inv")) return "Player";
        return "Combat";
    }

    private void enableScissor(int x, int y, int w, int h) {
        int factor = new net.minecraft.client.gui.ScaledResolution(mc).getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * factor, (mc.displayHeight - (y + h) * factor), w * factor, h * factor);
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
