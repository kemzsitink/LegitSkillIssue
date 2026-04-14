package com.client.legitskillissue.gui.component;

import net.minecraft.client.gui.Gui;
import java.awt.Color;

/**
 * Hình chữ nhật có màu — tương đương UIBlock của Elementa.
 * Hỗ trợ hover color và border.
 */
public class UIBlock extends UIComponent {

    private Color hoverColor = null;
    private Color borderColor = null;
    private boolean hovered = false;

    public UIBlock(float x, float y, float w, float h, Color color) {
        super(x, y, w, h);
        this.bgColor = color;
    }

    @Override
    public void draw(int mx, int my) {
        hovered = isHovered(mx, my);
        Color draw = (hovered && hoverColor != null) ? hoverColor : bgColor;
        float alpha = getAnimAlpha();

        Gui.drawRect(
            (int) getAnimX(), (int) getAnimY(),
            (int)(getAnimX() + getAnimW()), (int)(getAnimY() + getAnimH()),
            colorWithAlpha(draw, alpha)
        );

        if (borderColor != null) {
            int bx = (int) getAnimX(), by = (int) getAnimY();
            int bx2 = (int)(getAnimX() + getAnimW()), by2 = (int)(getAnimY() + getAnimH());
            int bc = colorWithAlpha(borderColor, alpha);
            Gui.drawRect(bx,      by,      bx2,     by + 1,  bc);
            Gui.drawRect(bx,      by2 - 1, bx2,     by2,     bc);
            Gui.drawRect(bx,      by,      bx + 1,  by2,     bc);
            Gui.drawRect(bx2 - 1, by,      bx2,     by2,     bc);
        }
    }

    public UIBlock setHoverColor(Color c)  { this.hoverColor  = c; return this; }
    public UIBlock setBorderColor(Color c) { this.borderColor = c; return this; }
    public boolean isHoveredNow()          { return hovered; }
}
