package com.client.legitskillissue.module;

import com.client.legitskillissue.event.EventBus;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private final Category category;
    private boolean enabled;
    private int keybind;
    private final List<NumberSetting> settings = new ArrayList<>();
    private final List<ModeSetting> modeSettings = new ArrayList<>();
    private final List<BooleanSetting> booleanSettings = new ArrayList<>();

    public Module(String name) {
        this(name, Category.COMBAT, Keyboard.KEY_NONE); 
    }

    public Module(String name, Category category) {
        this(name, category, Keyboard.KEY_NONE);
    }

    public Module(String name, Category category, int keybind) {
        this.name = name;
        this.category = category;
        this.keybind = keybind;
        this.enabled = false;
    }

    public String getName()    { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind()    { return keybind; }
    public void setKeybind(int key) { this.keybind = key; }

    protected NumberSetting addSetting(NumberSetting s) {
        settings.add(s);
        return s;
    }

    protected ModeSetting addSetting(ModeSetting s) {
        modeSettings.add(s);
        return s;
    }

    protected BooleanSetting addSetting(BooleanSetting s) {
        booleanSettings.add(s);
        return s;
    }

    public List<NumberSetting> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    public List<ModeSetting> getModeSettings() {
        return Collections.unmodifiableList(modeSettings);
    }

    public List<BooleanSetting> getBooleanSettings() {
        return Collections.unmodifiableList(booleanSettings);
    }

    public String getKeybindName() {
        return keybind == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(keybind);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            EventBus.INSTANCE.register(this);
            onEnable(); 
        } else {
            EventBus.INSTANCE.unregister(this);
            onDisable();
        }
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable()  {}
    protected void onDisable() {}

    // Legacy method supports (deprecated - moved to @EventTarget onUpdate)
    public void onTick() {}
    public void onMouseClick(net.minecraftforge.client.event.MouseEvent event) {}
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet)    { return false; }
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) { return false; }
}
