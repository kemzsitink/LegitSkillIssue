package com.client.legitskillissue.gui.component;

import java.util.ArrayList;
import java.util.List;

/**
 * Root container — tương đương Window của Elementa.
 * Quản lý toàn bộ cây component, gọi tick() và draw() theo thứ tự.
 */
public class Window {

    private final List<UIComponent> children = new ArrayList<>();
    private static final float ANIM_SPEED = 0.18f;

    public void addChild(UIComponent c) {
        children.add(c);
    }

    public void clear() {
        children.clear();
    }

    /** Gọi mỗi frame trước draw để cập nhật animation */
    public void tick(int mx, int my) {
        for (UIComponent c : children) {
            c.tick(ANIM_SPEED);
        }
    }

    /** Vẽ toàn bộ cây component */
    public void draw(int mx, int my) {
        for (UIComponent c : children) {
            c.draw(mx, my);
        }
    }

    public List<UIComponent> getChildren() {
        return children;
    }
}
