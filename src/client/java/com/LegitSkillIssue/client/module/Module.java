package com.LegitSkillIssue.client.module;

public class Module {
    private String name;
    private String description;
    private Category category;
    private boolean enabled;

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
    protected void onEnable() {}
    protected void onDisable() {}
}
