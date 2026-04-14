package com.client.legitskillissue.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import java.awt.Color;

/**
 * Text label — tương đương UIText của Elementa.
 */
public class UIText extends UIComponent {

    private String text;
    private Color color;
    private boolean shadow;

    private static final FontRenderer FR = Minecraft.getMinecraft().fontRendererObj;

    public UIText(String text, float x, float y, Color color) {
        super(x, y, FR.getStringWidth(text), FR.FONT_HEIGHT);
        this.text   = text;
        this.color  = color;
        this.shadow = false;
    }

    @Override
    public void draw(int mx, int my) {
        int c = colorWithAlpha(color, getAnimAlpha());
        if (shadow) {
            FR.drawStringWithShadow(text, (int) getAnimX(), (int) getAnimY(), c);
        } else {
            FR.drawString(text, (int) getAnimX(), (int) getAnimY(), c);
        }
    }

    public UIText setText(String t)     { this.text = t; this.width = FR.getStringWidth(t); return this; }
    public UIText setColor(Color c)     { this.color = c; return this; }
    public UIText setShadow(boolean s)  { this.shadow = s; return this; }
    public String getText()             { return text; }

    public static int textWidth(String s) { return FR.getStringWidth(s); }
}
