package com.legitskillissue.client.gui.components.settings;

import com.legitskillissue.client.gui.ElementaUtils;
import com.legitskillissue.client.gui.GuiConstants;
import com.legitskillissue.client.setting.BooleanSetting;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.events.UIClickEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import java.awt.Color;

public final class BooleanSettingComponent extends SettingComponent<BooleanSetting> {

    public BooleanSettingComponent(BooleanSetting setting) {
        super(setting);
        this.setHeight(new PixelConstraint(GuiConstants.SETTING_HEIGHT));
        
        UIText label = new UIText(setting.getName(), false);
        label.setY(new CenterConstraint());
        label.setTextScale(new PixelConstraint(0.7f));
        label.setColor(GuiConstants.TEXT_DIM);
        this.addChild(label);

        // Pill-style Toggle (Prototype style)
        UIRoundedRectangle track = new UIRoundedRectangle(3.5f);
        track.setWidth(new PixelConstraint(14.0f));
        track.setHeight(new PixelConstraint(7.0f));
        track.setX(new PixelConstraint(GuiConstants.PANEL_WIDTH - 38.0f));
        track.setY(new CenterConstraint());
        track.setColor(setting.getValue() ? GuiConstants.ACCENT : new Color(255, 255, 255, 30));
        this.addChild(track);

        UIBlock thumb = new UIBlock(Color.WHITE);
        thumb.setWidth(new PixelConstraint(5.0f));
        thumb.setHeight(new PixelConstraint(5.0f));
        thumb.setY(new CenterConstraint());
        thumb.setX(new PixelConstraint(setting.getValue() ? 8.0f : 1.0f));
        track.addChild(thumb);

        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                e.stopPropagation();
                setting.setValue(!setting.getValue());
                
                Color targetColor = setting.getValue() ? GuiConstants.ACCENT : new Color(255, 255, 255, 30);
                ElementaUtils.animateColor(track, targetColor, 0.2f);
                
                float targetX = setting.getValue() ? 8.0f : 1.0f;
                ElementaUtils.animateX(thumb, targetX, 0.2f);
                
                return Unit.INSTANCE;
            }
        });
    }
}
