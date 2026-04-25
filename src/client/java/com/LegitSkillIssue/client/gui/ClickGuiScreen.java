package com.LegitSkillIssue.client.gui;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.gui.components.CategoryPanel;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.constraints.*;
import java.awt.Color;

public class ClickGuiScreen extends WindowScreen {
    public ClickGuiScreen() {
        super(true, true, true, 0);

        // Khối chứa (Flex Row) tự động căn giữa màn hình và xếp các Category nằm ngang
        UIContainer flexRow = new UIContainer();
        flexRow.setX(new CenterConstraint());
        flexRow.setY(new CenterConstraint());
        flexRow.setWidth(new ChildBasedSizeConstraint());
        flexRow.setHeight(new ChildBasedSizeConstraint());
        getWindow().addChild(flexRow);

        for (Category category : Category.values()) {
            CategoryPanel panel = new CategoryPanel(category);
            // SiblingConstraint: Tự động xếp cạnh Component trước đó với khoảng cách 10px (như gap trong flexbox)
            panel.setX(new SiblingConstraint(10.0f));
            panel.setY(new PixelConstraint(0f));
            flexRow.addChild(panel);
        }
    }
}
