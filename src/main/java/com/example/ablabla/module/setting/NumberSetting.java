package com.example.ablabla.module.setting;

/**
 * A numeric (float) setting attached to a Module.
 * The GUI reads/writes this directly — no switch(idx) needed.
 */
public class NumberSetting {

    private final String name;
    private final String description;
    private final float  min;
    private final float  max;
    private final float  step;
    private float        value;

    public NumberSetting(String name, String description,
                         float min, float max, float step, float defaultValue) {
        this.name        = name;
        this.description = description;
        this.min         = min;
        this.max         = max;
        this.step        = step;
        this.value       = defaultValue;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public float  getMin()         { return min; }
    public float  getMax()         { return max; }
    public float  getStep()        { return step; }

    public float getValue() { return value; }

    public void setValue(float v) {
        // Snap to step, then clamp
        float snapped = Math.round(v / step) * step;
        this.value = Math.max(min, Math.min(max, snapped));
    }

    /** Convenience — returns value as int (for tick-based settings). */
    public int getInt() { return Math.round(value); }

    public long getLong() { return Math.round(value); }
}
