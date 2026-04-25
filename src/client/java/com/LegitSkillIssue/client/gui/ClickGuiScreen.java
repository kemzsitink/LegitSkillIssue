package com.LegitSkillIssue.client.gui;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.gui.components.CategoryPanel;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.constraints.PixelConstraint;

public class ClickGuiScreen extends WindowScreen {
    public ClickGuiScreen() {
        super(true, true, true, 0);

        int x = 20;
        int y = 20;
        int gap = 110;

        for (Category category : Category.values()) {
            CategoryPanel panel = new CategoryPanel(category);
            panel.setX(new PixelConstraint((float) x));
            panel.setY(new PixelConstraint((float) y));
            
            getWindow().addChild(panel);
            x += gap;
            
            if (x > 500) {
                x = 20;
                y += 200;
            }
        }
    }
}
