package com.client.legitskillissue.gui.component;

import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import java.awt.Color;

/**
 * Slider cho NumberSetting — drag để thay đổi giá trị.
 */
public class UISlider extends UIComponent {

    private static final Color TRACK_COLOR = new Color(0xFF, 0xFF, 0xFF, 50);
    private static final Color FILL_COLOR  = new Color(0x4F, 0xAC, 0xEE);
    private static final Color KNOB_COLOR  = Color.WHITE;

    private final NumberSetting setting;
    private float fillAnim = 0f;

    public UISlider(float x, float y, float w, NumberSetting setting) {
        super(x, y, w, 14);
        this.setting   = setting;
        this.fillAnim  = getPct();
    }

    @Override
    public void tick(float speed) {
        super.tick(speed);
        fillAnim = lerp(fillAnim, getPct(), 0.2f);
    }

    @Override
    public void draw(int mx, int my) {
        float alpha = getAnimAlpha();
        int bx = (int) getAnimX(), by = (int) getAnimY();
        int bw = (int) getAnimW();
        int trackY = by + 6;

        // Label + value
        net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.drawString(
            setting.getName(), bx, by, colorWithAlpha(new Color(0x82, 0x82, 0x8C), alpha));
        String val = String.valueOf(setting.getValue());
        int vw = UIText.textWidth(val);
        net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.drawString(
            val, bx + bw - vw, by, colorWithAlpha(FILL_COLOR, alpha));

        // Track
        Gui.drawRect(bx, trackY, bx + bw, trackY + 2, colorWithAlpha(TRACK_COLOR, alpha));

        // Fill
        int fillW = (int)(bw * fillAnim);
        Gui.drawRect(bx, trackY, bx + fillW, trackY + 2, colorWithAlpha(FILL_COLOR, alpha));

        // Knob
        Gui.drawRect(bx + fillW - 2, trackY - 3, bx + fillW + 2, trackY + 5, colorWithAlpha(KNOB_COLOR, alpha));

        // Drag
        if (Mouse.isButtonDown(0) && isHovered(mx, my)) {
            float newPct = Math.max(0f, Math.min(1f, (float)(mx - bx) / bw));
            setting.setValue(setting.getMin() + newPct * (setting.getMax() - setting.getMin()));
        }
    }

    private float getPct() {
        return (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
    }
}
