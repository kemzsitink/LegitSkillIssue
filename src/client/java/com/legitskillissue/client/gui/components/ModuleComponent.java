package com.legitskillissue.client.gui.components;

import com.legitskillissue.client.gui.GuiConstants;
import com.legitskillissue.client.gui.ElementaUtils;
import com.legitskillissue.client.gui.components.settings.BooleanSettingComponent;
import com.legitskillissue.client.gui.components.settings.ModeSettingComponent;
import com.legitskillissue.client.gui.components.settings.NumberSettingComponent;
import com.legitskillissue.client.module.Module;
import com.legitskillissue.client.setting.BooleanSetting;
import com.legitskillissue.client.setting.ModeSetting;
import com.legitskillissue.client.setting.NumberSetting;
import com.legitskillissue.client.setting.Setting;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.elementa.events.UIClickEvent;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import java.awt.Color;

public final class ModuleComponent extends UIRoundedRectangle {
    private final Module module;
    private final UIBlock settingsContainer;
    private boolean expanded = false;
    private final UIText text;
    private final UIBlock activeLine;
    private final UIText indicator;
    private final float totalSettingsHeight;

    public ModuleComponent(Module module) {
        super(4.0f); // Prototype: module rounded-md (6px -> 4px)
        this.module = module;
        this.setWidth(new RelativeConstraint(0.92f)); // Margin-x (prototype mx-2)
        this.setX(new CenterConstraint());
        this.setHeight(new PixelConstraint(GuiConstants.MODULE_HEIGHT));
        this.setColor(GuiConstants.BG_MODULE_IDLE);

        // --- Main Label ---
        text = new UIText(module.getName(), false);
        text.setX(new PixelConstraint(6.0f));
        text.setY(new PixelConstraint(GuiConstants.MODULE_HEIGHT / 2.0f - 3f)); // Manual center for robustness
        text.setTextScale(new PixelConstraint(0.8f));
        text.setColor(module.isEnabled() ? GuiConstants.TEXT_MAIN : GuiConstants.TEXT_DIM);
        this.addChild(text);

        activeLine = new UIBlock(GuiConstants.ACCENT);
        activeLine.setWidth(new PixelConstraint(2.0f));
        activeLine.setHeight(new PixelConstraint(GuiConstants.MODULE_HEIGHT * 0.6f));
        activeLine.setX(new PixelConstraint(2.0f));
        activeLine.setY(new PixelConstraint(GuiConstants.MODULE_HEIGHT * 0.2f));
        if (module.isEnabled()) {
            this.addChild(activeLine);
            this.setColor(GuiConstants.ACCENT_VIBE); // Use vibe color for active modules
        }

        indicator = new UIText("+", false);
        indicator.setX(new PixelConstraint(GuiConstants.PANEL_WIDTH * 0.92f - 10.0f));
        indicator.setY(new PixelConstraint(GuiConstants.MODULE_HEIGHT / 2.0f - 4f));
        indicator.setTextScale(new PixelConstraint(0.8f));
        indicator.setColor(GuiConstants.TEXT_DIM);
        if (!module.getSettings().isEmpty()) this.addChild(indicator);

        // --- Settings Container ---
        settingsContainer = new UIBlock(new Color(0, 0, 0, 0));
        settingsContainer.setX(new PixelConstraint(0f));
        settingsContainer.setY(new PixelConstraint(GuiConstants.MODULE_HEIGHT));
        settingsContainer.setWidth(new RelativeConstraint(1.0f));
        settingsContainer.setHeight(new PixelConstraint(0f));
        settingsContainer.enableEffect(new ScissorEffect());
        // Fix gap click leak identified by Agent 1
        settingsContainer.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent c, UIClickEvent e) {
                e.stopPropagation();
                return Unit.INSTANCE; // Consume click
            }
        });
        this.addChild(settingsContainer);

        // Settings Branch Visuals (Prototype: vertical line + leaves)
        UIBlock branchLine = new UIBlock(new Color(255, 255, 255, 25));
        branchLine.setX(new PixelConstraint(10.0f));
        branchLine.setWidth(new PixelConstraint(1.0f));
        branchLine.setHeight(new RelativeConstraint(1.0f));
        settingsContainer.addChild(branchLine);

        float yOffset = 4.0f;
        for (Setting<?> s : module.getSettings()) {
            // Add 'Leaf' line (horizontal branch)
            UIBlock leaf = new UIBlock(new Color(255, 255, 255, 25));
            leaf.setX(new PixelConstraint(10.0f));
            leaf.setY(new PixelConstraint(yOffset + 5.0f)); // Align with setting center
            leaf.setWidth(new PixelConstraint(4.0f));
            leaf.setHeight(new PixelConstraint(1.0f));
            settingsContainer.addChild(leaf);

            UIComponent comp = createSettingComponent(s);
            if (comp != null) {
                comp.setX(new PixelConstraint(16.0f));
                comp.setY(new PixelConstraint(yOffset));
                comp.setWidth(new PixelConstraint(GuiConstants.PANEL_WIDTH * 0.92f - 20.0f));
                settingsContainer.addChild(comp);
                yOffset += comp.getHeight() + 2.0f;
            }
        }
        this.totalSettingsHeight = yOffset + 2.0f;

        // --- Interactivity ---
        this.onMouseEnter(new Function1<UIComponent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp) {
                if (!module.isEnabled()) ElementaUtils.animateColor(ModuleComponent.this, GuiConstants.BG_MODULE_HOVER, 0.2f);
                return Unit.INSTANCE;
            }
        });

        this.onMouseLeave(new Function1<UIComponent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp) {
                if (!module.isEnabled()) ElementaUtils.animateColor(ModuleComponent.this, GuiConstants.BG_MODULE_IDLE, 0.2f);
                return Unit.INSTANCE;
            }
        });

        this.onMouseClick(new Function2<UIComponent, UIClickEvent, Unit>() {
            @Override
            public Unit invoke(UIComponent comp, UIClickEvent event) {
                if (event.getMouseButton() == 0) {
                    module.toggle();
                    updateState();
                } else if (event.getMouseButton() == 1) {
                    if (module.getSettings().isEmpty()) return Unit.INSTANCE;
                    expanded = !expanded;
                    indicator.setText(expanded ? "x" : "+");
                    indicator.setColor(expanded ? GuiConstants.TEXT_MAIN : GuiConstants.TEXT_DIM);
                    
                    float targetH = expanded ? GuiConstants.MODULE_HEIGHT + totalSettingsHeight : GuiConstants.MODULE_HEIGHT;
                    ElementaUtils.animateHeight(ModuleComponent.this, targetH, 0.35f);
                    ElementaUtils.animateHeight(settingsContainer, expanded ? totalSettingsHeight : 0f, 0.35f);
                }
                return Unit.INSTANCE;
            }
        });
    }

    private void updateState() {
        Color targetText = module.isEnabled() ? GuiConstants.TEXT_MAIN : GuiConstants.TEXT_DIM;
        ElementaUtils.animateColor(text, targetText, 0.2f);
        
        if (module.isEnabled()) {
            if (!getChildren().contains(activeLine)) addChild(activeLine);
            ElementaUtils.animateColor(this, GuiConstants.ACCENT_VIBE, 0.2f);
        } else {
            if (getChildren().contains(activeLine)) removeChild(activeLine);
            ElementaUtils.animateColor(this, GuiConstants.BG_MODULE_IDLE, 0.2f);
        }
    }

    private UIComponent createSettingComponent(Setting<?> s) {
        if (s instanceof BooleanSetting) return new BooleanSettingComponent((BooleanSetting) s);
        if (s instanceof NumberSetting) return new NumberSettingComponent((NumberSetting) s);
        if (s instanceof ModeSetting) return new ModeSettingComponent((ModeSetting) s);
        return null;
    }
}
