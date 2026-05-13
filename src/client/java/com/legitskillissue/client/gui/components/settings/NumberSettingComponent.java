package com.legitskillissue.client.gui.components.settings;

import com.legitskillissue.client.gui.GuiConstants;
import com.legitskillissue.client.setting.NumberSetting;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.events.UIClickEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import java.awt.Color;

public final class NumberSettingComponent extends SettingComponent<NumberSetting> {

    public NumberSettingComponent(NumberSetting setting) {
        super(setting);
        this.setHeight(new PixelConstraint(GuiConstants.SETTING_HEIGHT + 8f));

        UIText label = new UIText(setting.getName(), false);
        label.setTextScale(new PixelConstraint(0.7f));
        label.setColor(GuiConstants.TEXT_DIM);
        this.addChild(label);

        UIText valueText = new UIText(String.valueOf(setting.getValue()), false);
        valueText.setX(new PixelConstraint(GuiConstants.PANEL_WIDTH - 45.0f));
        valueText.setTextScale(new PixelConstraint(0.7f));
        valueText.setColor(Color.WHITE);
        this.addChild(valueText);

        UIBlock sliderBg = new UIBlock(new Color(255, 255, 255, 20));
        sliderBg.setY(new PixelConstraint(10f));
        sliderBg.setWidth(new RelativeConstraint(1.0f));
        sliderBg.setHeight(new PixelConstraint(2.0f));
        this.addChild(sliderBg);

        UIBlock sliderFill = new UIBlock(GuiConstants.ACCENT);
        float percent = (float) ((setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
        sliderFill.setWidth(new RelativeConstraint(percent));
        sliderFill.setHeight(new RelativeConstraint(1.0f));
        sliderBg.addChild(sliderFill);

        // Circular Handle (Prototype style)
        UIBlock handle = new UIBlock(Color.WHITE);
        handle.setWidth(new PixelConstraint(5.0f));
        handle.setHeight(new PixelConstraint(5.0f));
        handle.setX(new RelativeConstraint(percent));
        handle.setY(new PixelConstraint(-1.5f)); // Center on 2px bar
        sliderBg.addChild(handle);

        java.util.function.BiConsumer<Float, Float> updateSlider = (mouseX, mouseY) -> {
            float mouseRelX = mouseX - sliderBg.getLeft();
            float newPercent = Math.max(0f, Math.min(1f, mouseRelX / sliderBg.getWidth()));
            double newVal = setting.getMin() + (newPercent * (setting.getMax() - setting.getMin()));
            setting.setValue(Math.round(newVal * 10.0) / 10.0);
            
            sliderFill.setWidth(new RelativeConstraint(newPercent));
            handle.setX(new RelativeConstraint(newPercent));
            valueText.setText(String.valueOf(setting.getValue()));
        };

        sliderBg.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                e.stopPropagation();
                updateSlider.accept(e.getAbsoluteX(), e.getAbsoluteY());
                return Unit.INSTANCE;
            }
        });

        sliderBg.onMouseDrag(new Function4<UIComponent, Float, Float, Integer, Unit>() {
            @Override
            public Unit invoke(UIComponent c, Float x, Float y, Integer b) {
                // x and y are relative to c (sliderBg). updateSlider expects absolute coords.
                updateSlider.accept(c.getLeft() + x, c.getTop() + y);
                return Unit.INSTANCE;
            }
        });
    }
}
