package com.LegitSkillIssue.client.setting;

public abstract class Setting<T> {
    private String name;
    private T value;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
}
