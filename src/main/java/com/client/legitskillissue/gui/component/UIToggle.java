package com.client.legitskillissue.gui.component;

import net.minecraft.client.gui.Gui;
import java.awt.Color;

/**
 * Toggle switch animé — bật/tắt với lerp mượt.
 */
public class UIToggle extends UIComponent {

    private static final Color ON_COLOR  = new Color(0x4F, 0xAC, 0xEE);
    private static final Color OFF_COLOR = new Color(0x33, 0x33, 0x38);
    private static final Color KNOB      = Color.WHITE;

    private boolean value;
    private float   knobAnim; // 0.0 = off, 1.0 = on

    public UIToggle(float x, float y, boolean value) {
        super(x, y, 28, 14);
        this.value    = value;
        this.knobAnim = value ? 1f : 0f;
    }

    @Override
    public void tick(float speed) {
        super.tick(speed);
        knobAnim = lerp(knobAnim, value ? 1f : 0f, 0.25f);
    }

    @Override
    public void draw(int mx, int my) {
        float alpha = getAnimAlpha();
        int bx = (int) getAnimX(), by = (int) getAnimY();
        int bw = (int) getAnimW(), bh = (int) getAnimH();

        // Track — lerp màu giữa OFF và ON
        Color track = lerpColor(OFF_COLOR, ON_COLOR, knobAnim);
        Gui.drawRect(bx, by, bx + bw, by + bh, colorWithAlpha(track, alpha));

        // Knob
        int knobX = (int)(bx + knobAnim * (bw - bh));
        Gui.drawRect(knobX, by - 1, knobX + bh, by + bh + 1, colorWithAlpha(KNOB, alpha));
    }

    public void setValue(boolean v) { this.value = v; }
    public boolean getValue()       { return value; }

    private static Color lerpColor(Color a, Color b, float t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
    }
}
