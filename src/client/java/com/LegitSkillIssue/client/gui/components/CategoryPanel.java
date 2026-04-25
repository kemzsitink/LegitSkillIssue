package com.LegitSkillIssue.client.gui.components;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import java.awt.Color;

public class CategoryPanel extends UIBlock {
    public CategoryPanel(Category category) {
        this.setWidth(new PixelConstraint(110.0f));
        // Use ChildBasedSizeConstraint to auto-expand height based on modules
        this.setHeight(new ChildBasedSizeConstraint());
        this.setColor(new Color(25, 26, 30));

        // Header Panel
        UIBlock header = new UIBlock(new Color(114, 137, 218));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(25.0f));
        
        UIText title = new UIText(category.name());
        title.setX(new CenterConstraint());
        title.setY(new CenterConstraint());
        header.addChild(title);
        this.addChild(header);

        // Modules Container (Flex-column style layout)
        UIContainer listContainer = new UIContainer();
        listContainer.setWidth(new RelativeConstraint(1.0f));
        listContainer.setHeight(new ChildBasedSizeConstraint());
        // Position it right below the header
        listContainer.setY(new SiblingConstraint()); 
        this.addChild(listContainer);

        // Populate Modules
        ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .forEach(m -> {
                    ModuleComponent modComp = new ModuleComponent(m);
                    // Stack them vertically using SiblingConstraint
                    modComp.setY(new SiblingConstraint());
                    listContainer.addChild(modComp);
                });
    }
}
