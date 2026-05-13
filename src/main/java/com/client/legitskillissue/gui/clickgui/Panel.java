package com.client.legitskillissue.gui.clickgui;

import com.client.legitskillissue.gui.ClickGUI;
import com.client.legitskillissue.gui.animation.Animation;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;

/**
 * REFACTORED: Panel with search filtering, smooth animations, and scrolling.
 */
public class Panel extends Component {
    public Category category;
    public boolean open, dragging;
    public int dragX, dragY;
    private final ArrayList<ModuleButton> buttons = new ArrayList<>();
    private String searchQuery = "";
    
    // Scrolling
    public int scrollY = 0;
    private final int maxVisibleHeight = 200;
    
    // Animations
    private final Animation openAnimation;

    public Panel(Category category, int x, int y, int width) {
        super(x, y, width, 20);
        this.category = category;
        this.open = true;
        this.openAnimation = new Animation(1.0f, 0.15f);

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.getCategory() == category) {
                buttons.add(new ModuleButton(m, this));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        // Update animation
        openAnimation.setTarget(open ? 1.0f : 0.0f);
        openAnimation.update();
        float openProgress = openAnimation.getValue();

        // Header - Modern Shadow & Gradient
        boolean headerHovered = isHovered(mouseX, mouseY);
        int headerColorTop = ClickGUI.BG_DARK.getRGB();
        int headerColorBottom = headerHovered ? ClickGUI.BG_LIGHT.getRGB() : ClickGUI.BG_DARK.getRGB();
            
        // Shadow (only when header is visible or panel is open)
        RenderUtils.drawSoftShadow(x, y, x + width, y + height + (open ? maxVisibleHeight * openProgress : 0), 10, new Color(0, 0, 0, 150).getRGB());
        
        // Rounded Header
        RenderUtils.drawRoundedGradientRect(x, y, x + width, y + height, 4, headerColorTop, headerColorBottom);
        
        // Accent line at the bottom of header
        if (open) RenderUtils.drawRect(x, y + height - 1, x + width, y + height, ClickGUI.ACCENT.getRGB());

        // Category name
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
            category.getName(), x + 10, y + 6, -1);
        
        // Arrow indicator (centered vertically)
        String arrow = open ? "-" : "+";
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
            arrow, x + width - 15, y + 6, ClickGUI.ACCENT.getRGB());

        // Draw modules with animation and scrolling
        if (openProgress > 0.01f) {
            int buttonY = y + height - scrollY;
            int totalListHeight = 0;
            for (ModuleButton mb : buttons) {
                if (searchQuery.isEmpty() || mb.module.getName().toLowerCase().contains(searchQuery)) {
                    totalListHeight += mb.getHeight();
                }
            }

            // Scissor clipping
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);
            int scale = sr.getScaleFactor();
            
            int scissorX = x * scale;
            int scissorY = (mc.displayHeight - (y + height + maxVisibleHeight) * scale);
            int scissorW = width * scale;
            int scissorH = (int) (maxVisibleHeight * scale * openProgress);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

            // Panel body background
            RenderUtils.drawRect(x, y + height, x + width, y + height + (maxVisibleHeight * openProgress), ClickGUI.BG_DARK.getRGB());

            for (ModuleButton mb : buttons) {
                if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) continue;
                
                mb.x = x;
                mb.y = buttonY;
                mb.setAlpha(openProgress);
                mb.drawScreen(mouseX, mouseY);
                buttonY += (int) (mb.getHeight() * openProgress);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            
            // Modern Scrollbar
            if (totalListHeight > maxVisibleHeight) {
                float scrollPct = (float) scrollY / (totalListHeight - maxVisibleHeight);
                int barH = (int) (maxVisibleHeight * (maxVisibleHeight / (float) totalListHeight));
                int barY = y + height + (int) ((maxVisibleHeight - barH) * scrollPct);
                RenderUtils.drawRect(x + width - 2, barY, x + width, barY + barH, ClickGUI.ACCENT.getRGB());
            }
        }
    }

    public void handleScroll(int dWheel) {
        if (open) {
            int totalListHeight = 0;
            for (ModuleButton mb : buttons) {
                if (searchQuery.isEmpty() || mb.module.getName().toLowerCase().contains(searchQuery)) {
                    totalListHeight += mb.getHeight();
                }
            }
            
            if (totalListHeight > maxVisibleHeight) {
                scrollY -= dWheel / 10;
                if (scrollY < 0) scrollY = 0;
                if (scrollY > totalListHeight - maxVisibleHeight) scrollY = totalListHeight - maxVisibleHeight;
            } else {
                scrollY = 0;
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
                return true;
            } else if (mouseButton == 1) {
                open = !open;
                return true;
            }
        }
        
        if (open && openAnimation.getValue() > 0.5f) {
            // Check if mouse is within visible list bounds for interaction
            if (mouseX >= x && mouseX <= x + width && mouseY >= y + height && mouseY <= y + height + maxVisibleHeight) {
                for (ModuleButton mb : buttons) {
                    if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) {
                        continue;
                    }
                    if (mb.mouseClicked(mouseX, mouseY, mouseButton)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        if (open) {
            for (ModuleButton mb : buttons) {
                if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) {
                    continue;
                }
                mb.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (open) {
            for (ModuleButton mb : buttons) {
                if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) {
                    continue;
                }
                mb.keyTyped(typedChar, keyCode);
            }
        }
    }

    /**
     * Sets the search query for filtering modules.
     */
    public void setSearchQuery(String query) {
        this.searchQuery = query.toLowerCase();
    }

    /**
     * Gets the number of visible modules (after search filter).
     */
    public int getVisibleModuleCount() {
        if (searchQuery.isEmpty()) {
            return buttons.size();
        }
        
        int count = 0;
        for (ModuleButton mb : buttons) {
            if (mb.module.getName().toLowerCase().contains(searchQuery)) {
                count++;
            }
        }
        return count;
    }
}
