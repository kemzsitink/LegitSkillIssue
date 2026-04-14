package com.client.legitskillissue.gui.component;

import java.awt.Color;

/**
 * Base class cho mọi UI component — tương đương UIComponent của Elementa.
 * Mọi component đều có: vị trí, kích thước, màu nền, animation state.
 */
public abstract class UIComponent {

    protected float x, y, width, height;
    protected Color bgColor = new Color(0, 0, 0, 0);

    // Animation: mỗi property có current và target để lerp
    private float animX, animY, animW, animH;
    private float animAlpha = 1f;
    private float targetAlpha = 1f;

    public UIComponent(float x, float y, float width, float height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.animX = x; this.animY = y;
        this.animW = width; this.animH = height;
    }

    /** Gọi mỗi frame để lerp animation state */
    public void tick(float speed) {
        animX     = lerp(animX,     x,     speed);
        animY     = lerp(animY,     y,     speed);
        animW     = lerp(animW,     width, speed);
        animH     = lerp(animH,     height,speed);
        animAlpha = lerp(animAlpha, targetAlpha, speed);
    }

    public abstract void draw(int mx, int my);

    public boolean isHovered(int mx, int my) {
        return mx >= animX && mx <= animX + animW && my >= animY && my <= animY + animH;
    }

    // Fluent setters — tương đương .setX().setY() của Elementa
    public UIComponent setPos(float x, float y)       { this.x = x; this.y = y; return this; }
    public UIComponent setSize(float w, float h)      { this.width = w; this.height = h; return this; }
    public UIComponent setBgColor(Color c)            { this.bgColor = c; return this; }
    public UIComponent animateTo(float tx, float ty)  { this.x = tx; this.y = ty; return this; }
    public UIComponent fadeIn()  { this.targetAlpha = 1f; return this; }
    public UIComponent fadeOut() { this.targetAlpha = 0f; return this; }

    public float getAnimX()     { return animX; }
    public float getAnimY()     { return animY; }
    public float getAnimW()     { return animW; }
    public float getAnimH()     { return animH; }
    public float getAnimAlpha() { return animAlpha; }

    protected static float lerp(float a, float b, float t) {
        float d = b - a;
        if (Math.abs(d) < 0.001f) return b;
        return a + d * t;
    }

    /** Chuyển Color + alpha override sang ARGB int */
    protected static int colorWithAlpha(Color c, float alpha) {
        int a = (int)(c.getAlpha() * alpha);
        return (a << 24) | (c.getRGB() & 0x00FFFFFF);
    }
}
