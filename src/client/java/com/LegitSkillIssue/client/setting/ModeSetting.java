package com.LegitSkillIssue.client.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name, defaultMode);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
    }

    public List<String> getModes() { return modes; }
    
    public void cycle() {
        index = (index + 1) % modes.size();
        setValue(modes.get(index));
    }
}
