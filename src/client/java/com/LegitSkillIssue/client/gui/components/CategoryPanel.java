package com.LegitSkillIssue.client.gui.components;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import java.awt.Color;
import java.util.List;

public class CategoryPanel extends UIBlock {
    public CategoryPanel(Category category) {
        this.setWidth(new PixelConstraint(110.0f));
        this.setColor(new Color(25, 26, 30, 230)); // Deep dark grey semi-transparent

        // Blurple Header
        UIBlock header = new UIBlock(new Color(114, 137, 218));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(20.0f));
        
        // UIText with 'false' means shadow disabled for a cleaner, modern look
        UIText title = new UIText(category.name(), false);
        title.setX(new CenterConstraint());
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(0.85f));
        title.setColor(Color.WHITE);
        header.addChild(title);
        this.addChild(header);

        // Fetch modules for this specific category
        List<com.LegitSkillIssue.client.module.Module> categoryModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .toList();

        // Manual stacking logic using absolute Y-coordinates
        // This completely bypasses Elementa's buggy ChildBasedSizeConstraint for lists
        float currentYOffset = 21.0f; // Start 1px below the 20px header

        for (com.LegitSkillIssue.client.module.Module m : categoryModules) {
            ModuleComponent modComp = new ModuleComponent(m);
            // Absolutely position each module vertically
            modComp.setY(new PixelConstraint(currentYOffset));
            this.addChild(modComp);
            
            // Move pointer down by the height of the module (16px) + 1px gap
            currentYOffset += 17.0f;
        }

        // Set the panel's total height based on the final Y coordinate of the last module
        // + 2px for bottom padding
        this.setHeight(new PixelConstraint(currentYOffset + 2.0f));
    }
}
