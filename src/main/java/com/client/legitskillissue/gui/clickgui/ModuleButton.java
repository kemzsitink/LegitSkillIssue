package com.client.legitskillissue.gui.clickgui;

import com.client.legitskillissue.gui.ClickGUI;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.Color;
import java.util.ArrayList;

public class ModuleButton extends Component {
    public Module module;
    public Panel parent;
    public boolean extended;
    private final ArrayList<SettingComponent> components = new ArrayList<>();
    private float alpha = 1.0f; // For fade animations

    public ModuleButton(Module module, Panel parent) {
        super(0, 0, parent.width, 18);
        this.module = module;
        this.parent = parent;

        for (BooleanSetting s : module.getBooleanSettings()) components.add(new BoolComp(s, this));
        for (NumberSetting s : module.getSettings()) components.add(new NumComp(s, this));
        for (ModeSetting s : module.getModeSettings()) components.add(new ModeComp(s, this));
    }

    /**
     * Sets the alpha (opacity) for fade animations.
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (alpha < 0.01f) return; // Skip if fully transparent
        
        boolean hovered = isHovered(mouseX, mouseY);
        int baseColor = hovered ? new Color(35, 40, 55, 240).getRGB() : ClickGUI.BG_LIGHT.getRGB();
        int color = RenderUtils.colorWithAlpha(new Color(baseColor, true), (int) (255 * alpha));

        RenderUtils.drawRect(x, y, x + width, y + height, color);

        if (module.isEnabled()) {
            int accentColor = RenderUtils.colorWithAlpha(ClickGUI.ACCENT, (int) (255 * alpha));
            RenderUtils.drawRect(x, y, x + 2, y + height, accentColor);
        }

        int textColor = module.isEnabled() ? -1 : ClickGUI.TEXT_DIM.getRGB();
        int textAlpha = (int) (((textColor >> 24) & 0xFF) * alpha);
        textColor = (textAlpha << 24) | (textColor & 0xFFFFFF);
        
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(module.getName(), x + 8, y + 5, textColor);

        if (!components.isEmpty()) {
            Minecraft.getMinecraft().fontRendererObj.drawString(extended ? "." : "..", x + width - 15, y + 4, textColor);
        }
        
        // Tooltip on hover
        if (hovered && alpha > 0.9f) {
            String tooltip = module.getName();
            if (module.getKeybind() != 0) {
                tooltip += "\nKeybind: " + org.lwjgl.input.Keyboard.getKeyName(module.getKeybind());
            }
            tooltip += "\nLeft: Toggle | Right: Settings | Middle: Bind";
            ClickGUI.setTooltip((ClickGUI) Minecraft.getMinecraft().currentScreen, tooltip, mouseX + 10, mouseY);
        }

        if (extended) {
            int cY = y + height;
            for (SettingComponent sc : components) {
                sc.x = x;
                sc.y = cY;
                sc.width = width;
                sc.setAlpha(alpha);
                sc.drawScreen(mouseX, mouseY);
                cY += sc.getHeight();
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1 && !components.isEmpty()) {
                extended = !extended;
            } else if (mouseButton == 2) {
                ClickGUI.bindingModule = module;
            }
            return true;
        }
        if (extended) {
            for (SettingComponent sc : components) {
                if (sc.mouseClicked(mouseX, mouseY, mouseButton)) return true;
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (extended) {
            for (SettingComponent sc : components) {
                sc.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    public int getHeight() {
        int h = height;
        if (extended) {
            for (SettingComponent sc : components) h += sc.getHeight();
        }
        return h;
    }
}
