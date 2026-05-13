package com.legitskillissue.client.gui;

import com.legitskillissue.client.gui.components.CategoryPanel;
import com.legitskillissue.client.module.Category;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public final class ClickGuiScreen extends WindowScreen {
    private final UIBlock background;
    public static final Map<Category, Float> savedX = new HashMap<>();
    public static final Map<Category, Float> savedY = new HashMap<>();

    public ClickGuiScreen() {
        super(true, true, true, 0);

        Window window = getWindow();
        
        // --- Darkened Background with Fade-in ---
        background = new UIBlock(new Color(0, 0, 0, 0));
        background.setWidth(new RelativeConstraint(1.0f));
        background.setHeight(new RelativeConstraint(1.0f));
        window.addChild(background);
        
        ElementaUtils.animateColor(background, new Color(0, 0, 0, 100), 0.4f);

        // --- Create Panels ---
        float xOffset = 20.0f;
        float yOffset = 20.0f;
        float spacing = GuiConstants.PANEL_WIDTH + 10.0f;

        for (Category category : Category.values()) {
            float startX = savedX.getOrDefault(category, xOffset);
            float startY = savedY.getOrDefault(category, yOffset);

            CategoryPanel panel = new CategoryPanel(category, startX, startY);
            
            // Animation for panel entry
            panel.setY(new PixelConstraint(startY - 20f));
            panel.setColor(new Color(0,0,0,0));
            
            window.addChild(panel);
            
            ElementaUtils.animateY(panel, startY, 0.5f);
            ElementaUtils.animateColor(panel, GuiConstants.BG_PANEL, 0.5f);

            if (!savedX.containsKey(category)) {
                xOffset += spacing;
                // Wrap to next row if too wide
                if (xOffset + spacing > window.getWidth()) {
                    xOffset = 20.0f;
                    yOffset += 100.0f; 
                }
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
