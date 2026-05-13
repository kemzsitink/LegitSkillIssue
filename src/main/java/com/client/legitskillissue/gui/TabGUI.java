package com.client.legitskillissue.gui;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventRender2D;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TabGUI: Arrow-key driven menu for quick module toggling.
 */
public class TabGUI {

    public static TabGUI INSTANCE;
    private int currentCategoryIndex = 0;
    private int currentModuleIndex = 0;
    private int scrollOffset = 0;
    private final int maxVisibleItems = 12;
    private boolean extended = false;

    private final Category[] categories = Category.values();

    public TabGUI() {
        INSTANCE = this;
        com.client.legitskillissue.event.EventBus.INSTANCE.register(this);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) return;

        int x = 5;
        int y = 20; // Below watermark
        int width = 75;
        int height = 14;

        // Draw Categories
        for (int i = 0; i < categories.length; i++) {
            boolean selected = (i == currentCategoryIndex);
            int bgColor = selected ? new Color(0, 120, 255, 220).getRGB() : new Color(10, 10, 15, 180).getRGB();
            
            RenderUtils.drawRect(x, y + (i * height), x + width, y + ((i + 1) * height), bgColor);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(categories[i].getName(), x + 5, y + (i * height) + 3, -1);
            
            // Draw Modules if extended
            if (selected && extended) {
                List<Module> modules = getModulesForCategory(categories[i]);
                int moduleX = x + width + 2;
                int moduleWidth = 90;
                
                int visibleCount = Math.min(modules.size(), maxVisibleItems);
                
                // Adjust scrollOffset to keep currentModuleIndex visible
                if (currentModuleIndex < scrollOffset) {
                    scrollOffset = currentModuleIndex;
                } else if (currentModuleIndex >= scrollOffset + maxVisibleItems) {
                    scrollOffset = currentModuleIndex - maxVisibleItems + 1;
                }

                for (int j = 0; j < visibleCount; j++) {
                    int actualIndex = j + scrollOffset;
                    if (actualIndex >= modules.size()) break;
                    
                    Module m = modules.get(actualIndex);
                    boolean moduleSelected = (actualIndex == currentModuleIndex);
                    
                    int mBgColor = moduleSelected ? new Color(0, 120, 255, 220).getRGB() : new Color(10, 10, 15, 180).getRGB();
                    int textColor = m.isEnabled() ? new Color(80, 255, 120).getRGB() : -1;

                    RenderUtils.drawRect(moduleX, y + (j * height), moduleX + moduleWidth, y + ((j + 1) * height), mBgColor);
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(m.getName(), moduleX + 5, y + (j * height) + 3, textColor);
                }
                
                // Scroll indicators
                if (scrollOffset > 0) {
                    RenderUtils.drawRect(moduleX, y, moduleX + moduleWidth, y + 1, new Color(255, 255, 255, 100).getRGB());
                }
                if (scrollOffset + maxVisibleItems < modules.size()) {
                    RenderUtils.drawRect(moduleX, y + (visibleCount * height) - 1, moduleX + moduleWidth, y + (visibleCount * height), new Color(255, 255, 255, 100).getRGB());
                }
            }
        }
    }

    public void onKey(int key) {
        if (extended) {
            List<Module> modules = getModulesForCategory(categories[currentCategoryIndex]);
            switch (key) {
                case Keyboard.KEY_UP:
                    currentModuleIndex--;
                    if (currentModuleIndex < 0) currentModuleIndex = modules.size() - 1;
                    break;
                case Keyboard.KEY_DOWN:
                    currentModuleIndex++;
                    if (currentModuleIndex >= modules.size()) currentModuleIndex = 0;
                    break;
                case Keyboard.KEY_LEFT:
                    extended = false;
                    currentModuleIndex = 0;
                    scrollOffset = 0;
                    break;
                case Keyboard.KEY_RIGHT:
                case Keyboard.KEY_RETURN:
                    if (!modules.isEmpty()) {
                        modules.get(currentModuleIndex).toggle();
                    }
                    break;
            }
        } else {
            switch (key) {
                case Keyboard.KEY_UP:
                    currentCategoryIndex--;
                    if (currentCategoryIndex < 0) currentCategoryIndex = categories.length - 1;
                    break;
                case Keyboard.KEY_DOWN:
                    currentCategoryIndex++;
                    if (currentCategoryIndex >= categories.length) currentCategoryIndex = 0;
                    break;
                case Keyboard.KEY_RIGHT:
                case Keyboard.KEY_RETURN:
                    if (!getModulesForCategory(categories[currentCategoryIndex]).isEmpty()) {
                        extended = true;
                        currentModuleIndex = 0;
                        scrollOffset = 0;
                    }
                    break;
            }
        }
    }

    private List<Module> getModulesForCategory(Category cat) {
        return ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == cat)
                .collect(Collectors.toList());
    }
}
