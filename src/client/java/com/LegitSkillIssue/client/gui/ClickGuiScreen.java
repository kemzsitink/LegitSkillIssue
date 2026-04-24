package com.LegitSkillIssue.client.gui;

import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.ModuleManager;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ClickGuiScreen extends WindowScreen {
    private final Map<Category, UIBlock> panels = new HashMap<>();

    public ClickGuiScreen() {
        super(true, true, true, 0);
        
        int xOffset = 50;
        int yOffset = 50;
        int index = 0;

        for (Category category : Category.values()) {
            UIBlock panel = createPanel(category);
            
            int x = xOffset + (index % 4) * 250;
            int y = yOffset + (index / 4) * 350;
            
            panel.setX(new PixelConstraint(x));
            panel.setY(new PixelConstraint(y));
            
            getWindow().addChild(panel);
            panels.put(category, panel);
            index++;
        }
    }

    private UIBlock createPanel(Category category) {
        UIBlock panel = new UIBlock(new Color(20, 20, 25, 115));
        panel.setWidth(new PixelConstraint(230));
        panel.setHeight(new ChildBasedSizeConstraint());
        
        UIBlock header = new UIBlock(new Color(0, 0, 0, 100));
        header.setWidth(new RelativeConstraint(1f));
        header.setHeight(new PixelConstraint(35));
        
        UIText title = new UIText(category.name(), false);
        title.setX(new CenterConstraint());
        title.setY(new CenterConstraint());
        header.addChild(title);
        
        panel.addChild(header);

        UIContainer moduleList = new UIContainer();
        moduleList.setX(new PixelConstraint(0));
        moduleList.setY(new SiblingConstraint());
        moduleList.setWidth(new RelativeConstraint(1f));
        moduleList.setHeight(new ChildBasedSizeConstraint());
        
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            if (module.getCategory() == category) {
                moduleList.addChild(createModuleComponent(module));
            }
        }
        
        panel.addChild(moduleList);
        return panel;
    }

    private UIBlock createModuleComponent(Module module) {
        UIBlock component = new UIBlock(new Color(0, 0, 0, 0));
        component.setWidth(new RelativeConstraint(1f));
        component.setHeight(new PixelConstraint(30));
        
        UIText name = new UIText(module.getName(), false);
        name.setX(new PixelConstraint(10));
        name.setY(new CenterConstraint());
        component.addChild(name);

        component.onMouseClickConsumer((event) -> {
            module.toggle();
            updateModuleColor(component, module);
        });
        
        updateModuleColor(component, module);
        return component;
    }

    private void updateModuleColor(UIBlock component, Module module) {
        if (module.isEnabled()) {
            component.setColor(new Color(79, 172, 238, 50));
        } else {
            component.setColor(new Color(0, 0, 0, 0));
        }
    }
}
