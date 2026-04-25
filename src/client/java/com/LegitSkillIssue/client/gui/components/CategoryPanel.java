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
        this.setWidth(new PixelConstraint(100.0f)); // Nhỏ gọn hơn
        this.setHeight(new ChildBasedSizeConstraint());
        this.setColor(new Color(20, 20, 20, 200)); // Nền đen trong suốt hiện đại

        // Header Panel
        UIBlock header = new UIBlock(new Color(114, 137, 218, 255));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(18.0f)); // Chiều cao Header gọn lại
        
        UIText title = new UIText(category.name());
        title.setX(new CenterConstraint());
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(0.9f)); // Font chữ nhỏ thanh lịch
        title.setColor(Color.WHITE);
        header.addChild(title);
        this.addChild(header);

        // Modules Container
        UIContainer listContainer = new UIContainer();
        listContainer.setWidth(new RelativeConstraint(1.0f));
        listContainer.setHeight(new ChildBasedSizeConstraint());
        listContainer.setY(new SiblingConstraint(1.0f)); // Khoảng cách 1px với Header
        this.addChild(listContainer);

        // Render Modules
        ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .forEach(m -> {
                    ModuleComponent modComp = new ModuleComponent(m);
                    modComp.setY(new SiblingConstraint(1.0f)); // Khoảng cách 1px giữa các Module
                    listContainer.addChild(modComp);
                });
    }
}
