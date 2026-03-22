package com.example.ablabla.module;

import net.minecraft.client.Minecraft;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private boolean enabled;

    public Module(String name) {
        this.name = name;
        this.enabled = false;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println("[Ablabla-Logger] Module " + name + " toggled: " + (enabled ? "ON" : "OFF"));
        if (enabled) onEnable();
        else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable() {}
    protected void onDisable() {}

    // Event hooks
    public void onTick() {}
    public void onMouseClick(net.minecraftforge.client.event.MouseEvent event) {}
    
    // Return true to cancel packet
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) { return false; }
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) { return false; }
}
