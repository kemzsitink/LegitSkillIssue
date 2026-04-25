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
    private final Color idleColor = new Color(35, 36, 40);
    private final Color hoverColor = new Color(45, 46, 50);
    private final Color activeColor = new Color(114, 137, 218);

    public ModuleComponent(Module module) {
        this.module = module;
        
        this.setWidth(new RelativeConstraint(1.0f));
        this.setHeight(new PixelConstraint(25.0f));
        this.setColor(idleColor);

        final UIText text = new UIText(module.getName());
        text.setX(new PixelConstraint(10.0f));
        text.setY(new CenterConstraint());
        text.setColor(module.isEnabled() ? activeColor : Color.WHITE);
        this.addChild(text);

        // Java-safe Kotlin functional interfaces
        this.onMouseEnter(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                setColor(hoverColor);
                return Unit.INSTANCE;
            }
        });

        this.onMouseLeave(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                setColor(idleColor);
                return Unit.INSTANCE;
            }
        });

        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                module.toggle();
                text.setColor(module.isEnabled() ? activeColor : Color.WHITE);
                return Unit.INSTANCE;
            }
        });
    }
}
