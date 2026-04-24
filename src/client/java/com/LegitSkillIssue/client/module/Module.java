package com.LegitSkillIssue.client.module;

import com.LegitSkillIssue.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    
    private String name;
    private String description;
    private Category category;
    private boolean enabled;
    private List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) onEnable(); else onDisable();
    }
    
    public void toggle() { setEnabled(!enabled); }
    
    public void addSetting(Setting setting) {
        settings.add(setting);
    }
    
    public List<Setting> getSettings() {
        return settings;
    }

    protected void onEnable() {}
    protected void onDisable() {}
    
    public void onTick() {}
    public void onRender(DrawContext context, float tickDelta) {}
}
