package com.client.legitskillissue.gui.clickgui;

public abstract class Component {
    public int x, y, width, height;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void drawScreen(int mouseX, int mouseY);
    public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton);
    public void mouseReleased(int mouseX, int mouseY, int state) {}
    public void keyTyped(char typedChar, int keyCode) {}

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
