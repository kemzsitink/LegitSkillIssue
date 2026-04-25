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
        
        // Nền tối mờ
        UIBlock bg = new UIBlock(new Color(0, 0, 0, 100));
        bg.setWidth(new RelativeConstraint(1.0f));
        bg.setHeight(new RelativeConstraint(1.0f));
        window.addChild(bg);

        // Kích thước siêu gọn nhẹ (đã tính bù trừ GUI Scale)
        float startX = 20f;
        float startY = 20f;
        float panelWidth = 100f; // Thu nhỏ hơn phân nửa
        float gapX = 10f;
        
        float currentX = startX;

        for (Category category : Category.values()) {
            CategoryPanel panel = new CategoryPanel(category, currentX, startY, panelWidth);
            window.addChild(panel);

            currentX += (panelWidth + gapX);
            
            // Tự động xuống dòng nếu vượt quá 500px
            if (currentX > 500f) {
                currentX = startX;
                startY += 150f;
            }
        }
    }
}
