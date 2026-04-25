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
    
    private final Color idleBg = new Color(0, 0, 0, 0); 
    private final Color hoverBg = new Color(255, 255, 255, 20); 
    private final Color activeBg = new Color(79, 172, 238, 50); 
    
    private final Color textActive = Color.WHITE;
    private final Color textDim = new Color(170, 170, 170); 

    public ModuleComponent(Module module, float height) {
        this.module = module;
        
        // Use 100% of the dynamically calculated parent width
        this.setWidth(new RelativeConstraint(1.0f));
        this.setHeight(new PixelConstraint(height)); 
        this.setColor(module.isEnabled() ? activeBg : idleBg);

        // Text rendering
        final UIText text = new UIText(module.getName(), false);
        text.setX(new PixelConstraint(4.0f)); // Small left margin
        text.setY(new CenterConstraint());
        text.setTextScale(new PixelConstraint(1.0f)); // Pro design: Scale 1.0f for crisp text
        text.setColor(module.isEnabled() ? textActive : textDim);
        this.addChild(text);

        // Active Line
        UIBlock activeLine = new UIBlock(new Color(79, 172, 238));
        activeLine.setWidth(new PixelConstraint(2.0f)); 
        activeLine.setHeight(new RelativeConstraint(0.7f)); // 70% of module height
        activeLine.setX(new PixelConstraint(0.0f)); 
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

        // Click Event (Toggle)
        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                module.toggle();
                
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
