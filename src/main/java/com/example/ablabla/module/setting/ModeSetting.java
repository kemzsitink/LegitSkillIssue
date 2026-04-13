package com.example.ablabla.module.setting;

import com.example.ablabla.module.Module;
import java.util.Arrays;
import java.util.List;

public class ModeSetting {
    private final String name;
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        this.name = name;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
        if (this.index == -1) this.index = 0;
    }

    public String getName() {
        return name;
    }

    public List<String> getModes() {
        return modes;
    }

    public String getMode() {
        return modes.get(index);
    }

    public void setMode(String mode) {
        int i = modes.indexOf(mode);
        if (i != -1) index = i;
    }

    public void cycle() {
        if (index < modes.size() - 1) {
            index++;
        } else {
            index = 0;
        }
    }

    public boolean is(String mode) {
        return getMode().equalsIgnoreCase(mode);
    }
}
