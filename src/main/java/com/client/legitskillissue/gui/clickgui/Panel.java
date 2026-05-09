package com.client.legitskillissue.gui.clickgui;

import com.client.legitskillissue.gui.ClickGUI;
import com.client.legitskillissue.gui.animation.Animation;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

/**
 * REFACTORED: Panel with search filtering and smooth animations.
 */
public class Panel extends Component {
    public Category category;
    public boolean open, dragging;
    public int dragX, dragY;
    private final ArrayList<ModuleButton> buttons = new ArrayList<>();
    private String searchQuery = "";
    
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

        // Header
        boolean headerHovered = isHovered(mouseX, mouseY);
        int headerColor = headerHovered ? 
            RenderUtils.interpolateColor(ClickGUI.BG_DARK.getRGB(), ClickGUI.BG_LIGHT.getRGB(), 0.5f) :
            ClickGUI.BG_DARK.getRGB();
            
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, 3, headerColor);
        
        // Category name
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
            category.getName(), x + 8, y + 6, ClickGUI.ACCENT.getRGB());
        
        // Arrow indicator (animated)
        float arrowRotation = openProgress * 90; // 0° to 90°
        String arrow = open ? "v" : ">";
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
            arrow, x + width - 15, y + 6, -1);

        // Module count
        int visibleCount = getVisibleModuleCount();
        if (visibleCount > 0) {
            String countText = String.valueOf(visibleCount);
            int countW = Minecraft.getMinecraft().fontRendererObj.getStringWidth(countText);
            Minecraft.getMinecraft().fontRendererObj.drawString(
                countText, x + width - 25 - countW, y + 6, ClickGUI.TEXT_DIM.getRGB());
        }

        // Draw modules with animation
        if (openProgress > 0.01f) {
            int buttonY = y + height;
            int visibleButtons = 0;
            
            for (ModuleButton mb : buttons) {
                // Filter by search
                if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) {
                    continue;
                }
                
                mb.x = x;
                mb.y = buttonY;
                mb.setAlpha(openProgress); // Fade in/out
                mb.drawScreen(mouseX, mouseY);
                buttonY += (int) (mb.getHeight() * openProgress);
                visibleButtons++;
            }
            
            // Bottom shadow
            if (visibleButtons > 0) {
                int shadowAlpha = (int) (100 * openProgress);
                RenderUtils.drawRect(x, buttonY, x + width, buttonY + 1, 
                    RenderUtils.colorWithAlpha(ClickGUI.BG_DARK, shadowAlpha));
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
            for (ModuleButton mb : buttons) {
                if (!searchQuery.isEmpty() && !mb.module.getName().toLowerCase().contains(searchQuery)) {
                    continue;
                }
                if (mb.mouseClicked(mouseX, mouseY, mouseButton)) return true;
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
