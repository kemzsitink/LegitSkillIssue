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
    private final Color idleColor = new Color(0, 0, 0, 0); // Transparent when off
    private final Color hoverColor = new Color(255, 255, 255, 20); // Subtle white highlight
    private final Color activeColor = new Color(114, 137, 218, 150); // Blurple translucent

    public ModuleComponent(Module module) {
        this.module = module;
        
        // Exact pixel height, relative width
        this.setWidth(new RelativeConstraint(1.0f));
        this.setHeight(new PixelConstraint(16.0f)); 
        this.setColor(module.isEnabled() ? activeColor : idleColor);

        // No shadow text
        final UIText text = new UIText(module.getName(), false);
        text.setX(new PixelConstraint(6.0f)); // Left margin
        text.setY(new CenterConstraint());
        text.setTextScale(new PixelConstraint(0.8f));
        text.setColor(module.isEnabled() ? Color.WHITE : new Color(170, 170, 170));
        this.addChild(text);

        // Hover Effect
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

        // Click Event (Toggle)
        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                module.toggle();
                setColor(module.isEnabled() ? activeColor : hoverColor);
                text.setColor(module.isEnabled() ? Color.WHITE : new Color(170, 170, 170));
                return Unit.INSTANCE;
            }
        });
    }
}
