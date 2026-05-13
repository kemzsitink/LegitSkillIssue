package com.legitskillissue.client.gui.components.settings;

import com.legitskillissue.client.gui.GuiConstants;
import com.legitskillissue.client.setting.ModeSetting;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.events.UIClickEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import java.awt.Color;

public final class ModeSettingComponent extends SettingComponent<ModeSetting> {

    public ModeSettingComponent(ModeSetting setting) {
        super(setting);
        this.setHeight(new PixelConstraint(GuiConstants.SETTING_HEIGHT + 2.0f));

        UIText label = new UIText(setting.getName(), false);
        label.setY(new CenterConstraint());
        label.setTextScale(new PixelConstraint(0.7f));
        label.setColor(GuiConstants.TEXT_DIM);
        this.addChild(label);

        // Dropdown-style Button (Prototype style)
        UIRoundedRectangle btn = new UIRoundedRectangle(2.0f);
        btn.setX(new PixelConstraint(GuiConstants.PANEL_WIDTH - 55.0f));
        btn.setWidth(new PixelConstraint(35.0f));
        btn.setHeight(new RelativeConstraint(0.9f));
        btn.setY(new CenterConstraint());
        btn.setColor(new Color(0, 0, 0, 100));
        this.addChild(btn);

        UIText modeText = new UIText(setting.getValue(), false);
        modeText.setX(new CenterConstraint());
        modeText.setY(new CenterConstraint());
        modeText.setTextScale(new PixelConstraint(0.65f));
        modeText.setColor(Color.WHITE);
        btn.addChild(modeText);

        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                e.stopPropagation();
                setting.cycle();
                modeText.setText(setting.getValue());
                return Unit.INSTANCE;
            }
        });
    }
}
