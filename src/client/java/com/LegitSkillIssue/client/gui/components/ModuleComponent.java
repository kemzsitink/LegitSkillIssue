package com.LegitSkillIssue.client.gui.components;

import com.LegitSkillIssue.client.module.Module;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.events.UIClickEvent;
import gg.essential.elementa.UIComponent;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import java.awt.Color;

public class ModuleComponent extends UIBlock {
    private final Module module;
    private final Color idleColor = new Color(30, 30, 30, 150); // Đen mờ tinh tế
    private final Color hoverColor = new Color(50, 50, 50, 200); // Xám mờ khi di chuột
    private final Color activeColor = new Color(114, 137, 218, 255); // Màu chính Discord blurple

    public ModuleComponent(Module module) {
        this.module = module;
        
        this.setWidth(new RelativeConstraint(1.0f));
        this.setHeight(new PixelConstraint(16.0f)); // Rất nhỏ gọn
        this.setColor(module.isEnabled() ? activeColor : idleColor);

        final UIText text = new UIText(module.getName());
        text.setX(new PixelConstraint(5.0f)); // Căn lề trái 5px
        text.setY(new CenterConstraint());
        text.setTextScale(new PixelConstraint(0.8f)); // Text siêu mỏng
        text.setColor(Color.WHITE);
        this.addChild(text);

        // Java-safe Kotlin functional interfaces
        this.onMouseEnter(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                if (!module.isEnabled()) setColor(hoverColor);
                return Unit.INSTANCE;
            }
        });

        this.onMouseLeave(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                setColor(module.isEnabled() ? activeColor : idleColor);
                return Unit.INSTANCE;
            }
        });

        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                module.toggle();
                setColor(module.isEnabled() ? activeColor : hoverColor);
                return Unit.INSTANCE;
            }
        });
    }
}
