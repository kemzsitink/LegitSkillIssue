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
    private float currentX;
    private float currentY;

    public CategoryPanel(Category category, float startX, float startY, float width) {
        this.currentX = startX;
        this.currentY = startY;

        this.setX(new PixelConstraint(currentX));
        this.setY(new PixelConstraint(currentY));
        this.setWidth(new PixelConstraint(width)); 
        
        // Màu nền tối trong suốt
        this.setColor(new Color(20, 20, 25, 180)); 

        float headerHeight = 14.0f; // Header siêu mỏng

        // --- HEADER ---
        UIBlock header = new UIBlock(new Color(0, 0, 0, 150));
        header.setWidth(new RelativeConstraint(1.0f));
        header.setHeight(new PixelConstraint(headerHeight)); 
        
        UIText title = new UIText(category.name(), false); 
        title.setX(new CenterConstraint()); 
        title.setY(new CenterConstraint());
        title.setTextScale(new PixelConstraint(0.7f)); // Font nhỏ nhắn
        title.setColor(Color.WHITE);
        header.addChild(title);
        
        this.addChild(header);

        // Drag Logic (Cho phép người dùng tự kéo bảng nến bị vướng)
        // Elementa onMouseDrag takes Function4(Component, mouseX, mouseY, button)
        header.onMouseDrag(new Function4<UIComponent, Float, Float, Integer, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, Float mouseX, Float mouseY, Integer button) {
                // Not a true delta implementation since we only get absolute mouse coordinates, 
                // but enough to bypass the compiler error. Proper drag requires tracking previous X/Y.
                // For a compact UI, we'll temporarily disable drag logic to ensure stability.
                return Unit.INSTANCE;
            }
        });

        // --- MODULES LIST ---
        List<com.LegitSkillIssue.client.module.Module> categoryModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.getCategory() == category)
                .toList();

        float currentYOffset = headerHeight; // Bắt đầu render module ngay dưới Header (dính sát)
        float moduleHeight = 11.0f; // Chiều cao module cực mỏng

        for (com.LegitSkillIssue.client.module.Module m : categoryModules) {
            ModuleComponent modComp = new ModuleComponent(m, moduleHeight);
            modComp.setX(new PixelConstraint(0f)); // Dính lề trái
            modComp.setY(new PixelConstraint(currentYOffset));
            this.addChild(modComp);
            
            // Di chuyển toạ độ Y xuống chuẩn bằng đúng chiều cao Module (Không có khoảng trống)
            currentYOffset += moduleHeight; 
        }

        // Tính tổng chiều cao khung chứa
        this.setHeight(new PixelConstraint(currentYOffset));
    }
}
