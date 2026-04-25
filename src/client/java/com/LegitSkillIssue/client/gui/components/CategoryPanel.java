package com.LegitSkillIssue.client.gui.components;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import java.awt.Color;
import java.util.List;

public class CategoryPanel extends UIBlock {

    public CategoryPanel(Category category, float startX, float startY) {
        this.setX(new PixelConstraint(startX));
        this.setY(new PixelConstraint(startY));
        this.setWidth(new PixelConstraint(230.0f)); // Tailwind width: w-[230px]
        
        // Panel Background: rgba(20, 20, 25, 0.45) mapped to roughly 115 alpha, but slightly more opaque for game visibility
        this.setColor(new Color(20, 20, 25, 150)); 

        // --- HEADER ---
        // Header Background: bg-black/40
        UIBlock header = new UIBlock(new Color(0, 0, 0, 102));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(28.0f)); // Tailwind: p-3.5
        
        UIText title = new UIText(category.name(), false); 
        title.setX(new PixelConstraint(10.0f)); // Left aligned
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(0.9f));
        title.setColor(Color.WHITE);
        header.addChild(title);
        
        this.addChild(header);

        // --- MODULES LIST ---
        List<com.LegitSkillIssue.client.module.Module> categoryModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .toList();

        float currentYOffset = 30.0f; // Start below header (28px) + 2px padding

        for (com.LegitSkillIssue.client.module.Module m : categoryModules) {
            ModuleComponent modComp = new ModuleComponent(m);
            modComp.setX(new CenterConstraint()); // Center in panel
            modComp.setY(new PixelConstraint(currentYOffset));
            this.addChild(modComp);
            
            // Height of module (24px) + gap (2px)
            currentYOffset += 26.0f; 
        }

        // Total height + bottom padding
        this.setHeight(new PixelConstraint(currentYOffset + 4.0f));
    }
}
