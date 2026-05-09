package com.client.legitskillissue.gui.clickgui;

import com.client.legitskillissue.gui.ClickGUI;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.Color;

public abstract class SettingComponent extends Component {
    public ModuleButton parent;
    protected float alpha = 1.0f;

    public SettingComponent(ModuleButton parent) {
        super(0, 0, parent.width, 18);
        this.parent = parent;
    }

    public int getHeight() { return 18; }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
    }
}

class BoolComp extends SettingComponent {
    private final BooleanSetting setting;

    public BoolComp(BooleanSetting setting, ModuleButton parent) {
        super(parent);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtils.drawRect(x, y, x + width, y + getHeight(), new Color(20, 25, 35, 240).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(setting.getName(), x + 12, y + 5, -1);

        // Toggle Pill UI
        int pillW = 18, pillH = 8;
        int pillX = x + width - pillW - 10, pillY = y + 5;
        int pillColor = setting.getValue() ? ClickGUI.ACCENT.getRGB() : new Color(60, 60, 70).getRGB();
        RenderUtils.drawRoundedRect(pillX, pillY, pillX + pillW, pillY + pillH, 4, pillColor);

        // Knob
        float knobX = setting.getValue() ? pillX + pillW - 4 : pillX + 4;
        RenderUtils.drawFilledCircle(knobX, pillY + 4, 4, -1);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            setting.setValue(!setting.getValue());
            return true;
        }
        return false;
    }
}

class NumComp extends SettingComponent {
    private final NumberSetting setting;
    private boolean sliding;

    public NumComp(NumberSetting setting, ModuleButton parent) {
        super(parent);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtils.drawRect(x, y, x + width, y + getHeight(), new Color(20, 25, 35, 240).getRGB());

        if (sliding) {
            double diff = Math.min(width - 24, Math.max(0, mouseX - (x + 12)));
            float newVal = (float) (setting.getMin() + (diff / (width - 24)) * (setting.getMax() - setting.getMin()));
            setting.setValue(newVal);
        }

        float valPerc = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int trackX = x + 12, trackY = y + 14, trackW = width - 24;

        // Track
        RenderUtils.drawRect(trackX, trackY, trackX + trackW, trackY + 1, new Color(60, 60, 70).getRGB());
        RenderUtils.drawRect(trackX, trackY, trackX + (int) (trackW * valPerc), trackY + 1, ClickGUI.ACCENT.getRGB());

        // Rounded Knob
        RenderUtils.drawFilledCircle(trackX + (trackW * valPerc), trackY + 0.5f, 3.5f, -1);

        Minecraft.getMinecraft().fontRendererObj.drawString(setting.getName(), x + 12, y + 3, -1);
        String valStr = String.format("%.2f", setting.getValue());
        Minecraft.getMinecraft().fontRendererObj.drawString(valStr, x + width - Minecraft.getMinecraft().fontRendererObj.getStringWidth(valStr) - 12, y + 3, ClickGUI.TEXT_DIM.getRGB());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            sliding = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        sliding = false;
    }
}

class ModeComp extends SettingComponent {
    private final ModeSetting setting;

    public ModeComp(ModeSetting setting, ModuleButton parent) {
        super(parent);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtils.drawRect(x, y, x + width, y + getHeight(), new Color(20, 25, 35, 240).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(setting.getName(), x + 12, y + 5, -1);
        String mode = setting.getMode();
        Minecraft.getMinecraft().fontRendererObj.drawString(mode, x + width - Minecraft.getMinecraft().fontRendererObj.getStringWidth(mode) - 12, y + 5, ClickGUI.ACCENT.getRGB());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            setting.cycle();
            return true;
        }
        return false;
    }
}
