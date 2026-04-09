package com.example.ablabla.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private boolean enabled;
    private int keybind; // LWJGL key code, Keyboard.KEY_NONE = no bind

    public Module(String name) {
        this(name, Keyboard.KEY_NONE);
    }

    public Module(String name, int keybind) {
        this.name = name;
        this.keybind = keybind;
        this.enabled = false;
    }

    public String getName()    { return name; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind()    { return keybind; }
    public void setKeybind(int key) { this.keybind = key; }

    public String getKeybindName() {
        return keybind == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(keybind);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable()  {}
    protected void onDisable() {}

    public void onTick() {}
    public void onMouseClick(net.minecraftforge.client.event.MouseEvent event) {}

    /** Return true to cancel the outgoing packet. */
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet)    { return false; }
    /** Return true to cancel the incoming packet. */
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) { return false; }
}
