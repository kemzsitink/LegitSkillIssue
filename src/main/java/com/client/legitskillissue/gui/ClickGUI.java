package com.client.legitskillissue.gui;

import com.client.legitskillissue.gui.clickgui.Panel;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 * REFACTORED: Enhanced ClickGUI with search, tooltips, and better UX.
 * 
 * IMPROVEMENTS:
 * - Search bar for quick module access
 * - Tooltips showing module descriptions and keybinds
 * - Smooth animations
 * - Better visual feedback
 * - Keyboard navigation support
 */
public class ClickGUI extends GuiScreen {
    private final ArrayList<Panel> panels = new ArrayList<>();
    public static Module bindingModule = null;

    // Theme Colors
    public static final Color ACCENT = new Color(40, 150, 255);
    public static final Color BG_DARK = new Color(10, 15, 25, 230);
    public static final Color BG_LIGHT = new Color(25, 30, 40, 240);
    public static final Color TEXT_DIM = new Color(180, 180, 190);
    public static final Color SUCCESS = new Color(80, 200, 120);
    public static final Color WARNING = new Color(255, 180, 0);

    // Search
    private GuiTextField searchField;
    private String searchQuery = "";
    
    // Tooltip
    private String tooltipText = null;
    private int tooltipX, tooltipY;

    public ClickGUI() {
        int x = 20;
        for (Category category : Category.values()) {
            panels.add(new Panel(category, x, 20, 115));
            x += 125;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        
        // Initialize search field
        searchField = new GuiTextField(0, fontRendererObj, width / 2 - 100, 5, 200, 20);
        searchField.setMaxStringLength(50);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setTextColor(0xFFFFFF);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Background with blur effect
        drawRect(0, 0, width, height, new Color(0, 0, 0, 150).getRGB());
        
        // Draw search bar
        drawSearchBar();
        
        // Draw panels
        for (Panel panel : panels) {
            panel.setSearchQuery(searchQuery);
            panel.drawScreen(mouseX, mouseY);
        }
        
        // Binding overlay
        if (bindingModule != null) {
            drawRect(0, 0, width, height, new Color(0, 0, 0, 200).getRGB());
            
            // Binding box
            int boxW = 300, boxH = 80;
            int boxX = width / 2 - boxW / 2;
            int boxY = height / 2 - boxH / 2;
            
            RenderUtils.drawRoundedRect(boxX, boxY, boxX + boxW, boxY + boxH, 5, BG_DARK.getRGB());
            RenderUtils.drawRoundedRect(boxX, boxY, boxX + boxW, boxY + 3, 5, ACCENT.getRGB());
            
            drawCenteredString(fontRendererObj, "Binding: " + bindingModule.getName(), width / 2, boxY + 20, ACCENT.getRGB());
            drawCenteredString(fontRendererObj, "Press any key...", width / 2, boxY + 35, -1);
            drawCenteredString(fontRendererObj, "ESC to clear | CLICK to cancel", width / 2, boxY + 55, TEXT_DIM.getRGB());
        }
        
        // Draw tooltip last (on top)
        if (tooltipText != null && bindingModule == null) {
            drawTooltip(tooltipText, tooltipX, tooltipY);
        }
        
        tooltipText = null; // Reset for next frame
    }

    private void drawSearchBar() {
        int barX = width / 2 - 100;
        int barY = 5;
        int barW = 200;
        int barH = 20;
        
        // Background
        RenderUtils.drawRoundedRect(barX, barY, barX + barW, barY + barH, 3, BG_DARK.getRGB());
        
        // Border (accent if focused)
        if (searchField.isFocused()) {
            RenderUtils.drawRoundedRect(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 3, 
                RenderUtils.colorWithAlpha(ACCENT, 100));
        }
        
        // Search icon (magnifying glass)
        fontRendererObj.drawString("\u26B2", barX + 5, barY + 6, TEXT_DIM.getRGB());
        
        // Text field
        searchField.xPosition = barX + 20;
        searchField.yPosition = barY + 6;
        searchField.width = barW - 25;
        searchField.drawTextBox();
        
        // Placeholder text
        if (searchField.getText().isEmpty() && !searchField.isFocused()) {
            fontRendererObj.drawString("Search modules...", barX + 20, barY + 6, TEXT_DIM.getRGB());
        }
        
        // Result count
        if (!searchQuery.isEmpty()) {
            int count = getSearchResultCount();
            String countText = count + " result" + (count != 1 ? "s" : "");
            fontRendererObj.drawString(countText, barX + barW + 10, barY + 6, TEXT_DIM.getRGB());
        }
    }

    private void drawTooltip(String text, int x, int y) {
        String[] lines = text.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            int w = fontRendererObj.getStringWidth(line);
            if (w > maxWidth) maxWidth = w;
        }
        
        int tooltipW = maxWidth + 12;
        int tooltipH = lines.length * (fontRendererObj.FONT_HEIGHT + 2) + 6;
        
        // Keep tooltip on screen
        if (x + tooltipW > width) x = width - tooltipW - 5;
        if (y + tooltipH > height) y = height - tooltipH - 5;
        
        // Shadow
        RenderUtils.drawShadow(x, y, x + tooltipW, y + tooltipH, 3, new Color(0, 0, 0, 100).getRGB());
        
        // Background
        RenderUtils.drawRoundedRect(x, y, x + tooltipW, y + tooltipH, 4, BG_DARK.getRGB());
        
        // Border
        RenderUtils.drawRoundedRect(x, y, x + tooltipW, y + 2, 4, ACCENT.getRGB());
        
        // Text
        int textY = y + 5;
        for (String line : lines) {
            fontRendererObj.drawString(line, x + 6, textY, -1);
            textY += fontRendererObj.FONT_HEIGHT + 2;
        }
    }

    private int getSearchResultCount() {
        int count = 0;
        for (Panel panel : panels) {
            count += panel.getVisibleModuleCount();
        }
        return count;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Search field
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        
        if (bindingModule != null) {
            if (mouseButton == 0) bindingModule = null;
            return;
        }
        
        // Panels (reverse order for proper z-index)
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).mouseClicked(mouseX, mouseY, mouseButton)) {
                Panel p = panels.remove(i);
                panels.add(p); // Move to front
                return;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (bindingModule != null) return;
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Binding mode
        if (bindingModule != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                bindingModule.setKeybind(Keyboard.KEY_NONE);
            } else {
                bindingModule.setKeybind(keyCode);
            }
            bindingModule = null;
            return;
        }
        
        // Search field
        if (searchField.isFocused()) {
            searchField.textboxKeyTyped(typedChar, keyCode);
            searchQuery = searchField.getText().toLowerCase();
            return;
        }
        
        // Panel key events
        for (Panel panel : panels) {
            panel.keyTyped(typedChar, keyCode);
        }
        
        // ESC to close
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    /**
     * Sets tooltip to be displayed this frame.
     * Call from components during drawScreen.
     */
    public static void setTooltip(ClickGUI gui, String text, int x, int y) {
        if (gui != null) {
            gui.tooltipText = text;
            gui.tooltipX = x;
            gui.tooltipY = y;
        }
    }
}
