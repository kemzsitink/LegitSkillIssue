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
    
    // Tailwind Colors
    private final Color idleBg = new Color(0, 0, 0, 0); // transparent
    private final Color hoverBg = new Color(255, 255, 255, 13); // hover:bg-white/5
    private final Color activeBg = new Color(79, 172, 238, 38); // rgba(79, 172, 238, 0.15)
    
    private final Color textActive = Color.WHITE;
    private final Color textDim = new Color(160, 160, 165); // #a0a0a5

    public ModuleComponent(Module module) {
        this.module = module;
        
        // Size: slightly less than panel width (230 - 16 = 214)
        this.setWidth(new PixelConstraint(214.0f)); 
        this.setHeight(new PixelConstraint(24.0f));
        this.setColor(module.isEnabled() ? activeBg : idleBg);

        // Text
        final UIText text = new UIText(module.getName(), false);
        text.setX(new PixelConstraint(8.0f)); // Left padding
        text.setY(new CenterConstraint());
        text.setTextScale(new PixelConstraint(0.85f));
        text.setColor(module.isEnabled() ? textActive : textDim);
        this.addChild(text);

        // Active Line (Left Border)
        UIBlock activeLine = new UIBlock(new Color(79, 172, 238));
        activeLine.setWidth(new PixelConstraint(2.0f));
        activeLine.setHeight(new RelativeConstraint(0.6f)); // 60% height
        activeLine.setX(new PixelConstraint(2.0f));
        activeLine.setY(new CenterConstraint());
        if (module.isEnabled()) {
            this.addChild(activeLine);
        }

        // Hover Effect
        this.onMouseEnter(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                if (!module.isEnabled()) setColor(hoverBg);
                return Unit.INSTANCE;
            }
        });

        this.onMouseLeave(new Function1<Object, Unit>() {
            @Override
            public Unit invoke(Object event) {
                setColor(module.isEnabled() ? activeBg : idleBg);
                return Unit.INSTANCE;
            }
        });

        // Click Event
        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                module.toggle();
                
                // Update visuals
                setColor(module.isEnabled() ? activeBg : hoverBg);
                text.setColor(module.isEnabled() ? textActive : textDim);
                
                if (module.isEnabled() && !getChildren().contains(activeLine)) {
                    addChild(activeLine);
                } else if (!module.isEnabled() && getChildren().contains(activeLine)) {
                    removeChild(activeLine);
                }
                return Unit.INSTANCE;
            }
        });
    }
}
