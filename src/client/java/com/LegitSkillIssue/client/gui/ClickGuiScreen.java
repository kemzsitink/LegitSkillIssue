package com.LegitSkillIssue.client.gui;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.gui.components.CategoryPanel;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.*;
import java.awt.Color;

public class ClickGuiScreen extends WindowScreen {
    public ClickGuiScreen() {
        super(true, true, true, 0);

        Window window = getWindow();
        
        // Dark overlay background for the whole screen
        UIBlock bg = new UIBlock(new Color(0, 0, 0, 100));
        bg.setWidth(new RelativeConstraint(1.0f));
        bg.setHeight(new RelativeConstraint(1.0f));
        window.addChild(bg);

        // Mathematical Grid Layout System (Tailwind Proportions)
        float startX = 20f;
        float startY = 20f;
        float panelWidth = 230f; // New Tailwind width
        float gapX = 20f;
        
        float currentX = startX;

        for (Category category : Category.values()) {
            CategoryPanel panel = new CategoryPanel(category, currentX, startY);
            window.addChild(panel);

            // Move to next column
            currentX += (panelWidth + gapX);
            
            // Wrap to next row if it exceeds a reasonable width
            if (currentX > 800f) {
                currentX = startX;
                startY += 300f; // Increase height for wrapping
            }
        }
    }
}
