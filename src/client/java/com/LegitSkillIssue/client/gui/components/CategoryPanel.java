package com.LegitSkillIssue.client.gui.components;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import java.awt.Color;
import java.util.List;

public class CategoryPanel extends UIBlock {

    public CategoryPanel(Category category, float startX, float startY, float width) {
        this.setX(new PixelConstraint(startX));
        this.setY(new PixelConstraint(startY));
        this.setWidth(new PixelConstraint(width)); 
        
        // Panel Background: rgba(20, 20, 25, 0.7)
        this.setColor(new Color(20, 20, 25, 180)); 

        // --- 2. HEADER ---
        float headerHeight = 14.0f; // Font height (9) + Padding (5)
        UIBlock header = new UIBlock(new Color(0, 0, 0, 150));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(headerHeight)); 
        
        UIText title = new UIText(category.name(), false); 
        title.setX(new CenterConstraint()); 
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(1.0f)); // Crisp text
        title.setColor(Color.WHITE);
        header.addChild(title);
        
        this.addChild(header);

        // Drag Logic 
        header.onMouseDrag(new Function4<UIComponent, Float, Float, Integer, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, Float mouseX, Float mouseY, Integer button) {
                return Unit.INSTANCE;
            }
        });

        // --- 3. MODULES LIST ---
        List<com.LegitSkillIssue.client.module.Module> categoryModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .toList();

        float currentYOffset = headerHeight; 
        float moduleHeight = 13.0f; // Font height (9) + Padding (4)

        for (com.LegitSkillIssue.client.module.Module m : categoryModules) {
            ModuleComponent modComp = new ModuleComponent(m, moduleHeight);
            modComp.setX(new PixelConstraint(0f)); 
            modComp.setY(new PixelConstraint(currentYOffset));
            this.addChild(modComp);
            
            currentYOffset += moduleHeight; 
        }

        this.setHeight(new PixelConstraint(currentYOffset));
    }
}
