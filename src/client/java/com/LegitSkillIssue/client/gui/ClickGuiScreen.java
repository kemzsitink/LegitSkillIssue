package com.LegitSkillIssue.client.gui;

import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.gui.components.CategoryPanel;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.*;
import net.minecraft.client.MinecraftClient;
import java.awt.Color;
import java.util.List;

public class ClickGuiScreen extends WindowScreen {
    public ClickGuiScreen() {
        super(true, true, true, 0);

        Window window = getWindow();
        
        UIBlock bg = new UIBlock(new Color(0, 0, 0, 100));
        bg.setWidth(new RelativeConstraint(1.0f));
        bg.setHeight(new RelativeConstraint(1.0f));
        window.addChild(bg);

        // Kích thước chuẩn
        float startX = 20f;
        float startY = 20f;
        float gapX = 15f; // Khoảng cách giữa các cột
        
        float currentX = startX;
        float maxRowHeight = 0; // Để quản lý xuống dòng nếu cần thiết

        for (Category category : Category.values()) {
            
            // --- TÍNH TOÁN CHIỀU RỘNG ĐỘNG (PRO DESIGN) ---
            float maxTextWidth = MinecraftClient.getInstance().textRenderer.getWidth(category.name());
            
            List<com.LegitSkillIssue.client.module.Module> catMods = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category).toList();
                
            for (com.LegitSkillIssue.client.module.Module m : catMods) {
                float width = MinecraftClient.getInstance().textRenderer.getWidth(m.getName());
                if (width > maxTextWidth) {
                    maxTextWidth = width;
                }
            }
            
            // Width = chữ dài nhất + 20px padding (vừa vặn không thừa một chút nào)
            float panelWidth = maxTextWidth + 20.0f;
            
            // --- TẠO PANEL ---
            CategoryPanel panel = new CategoryPanel(category, currentX, startY, panelWidth);
            window.addChild(panel);

            // Chuẩn bị vị trí X cho panel tiếp theo
            currentX += (panelWidth + gapX);
            
            // Nếu panel cuối cùng làm dòng quá dài, xuống dòng
            if (currentX > 600f) { // Giới hạn chiều ngang
                currentX = startX;
                startY += 250f; // Khoảng cách xuống dòng (cần estimate lớn hơn panel dài nhất)
            }
        }
    }
}
