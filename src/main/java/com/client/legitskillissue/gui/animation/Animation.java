package com.client.legitskillissue.gui.animation;

/**
 * Smooth animation system for GUI elements.
 * Supports multiple easing functions for natural motion.
 */
public class Animation {
    private float value;
    private float target;
    private final float speed;
    private final EasingFunction easing;

    public enum EasingFunction {
        LINEAR,
        QUAD_OUT,
        CUBIC_OUT,
        EXPO_OUT,
        ELASTIC_OUT
    }

    public Animation(float initialValue, float speed, EasingFunction easing) {
        this.value = initialValue;
        this.target = initialValue;
        this.speed = speed;
        this.easing = easing;
    }

    public Animation(float initialValue, float speed) {
        this(initialValue, speed, EasingFunction.QUAD_OUT);
    }

    public void update() {
        if (isDone()) {
            value = target;
            return;
        }

        float delta = target - value;
        float factor = speed;

        switch (easing) {
            case QUAD_OUT:
                factor = 1 - (float) Math.pow(1 - speed, 3);
                break;
            case CUBIC_OUT:
                factor = 1 - (float) Math.pow(1 - speed, 4);
                break;
            case EXPO_OUT:
                factor = 1 - (float) Math.pow(2, -10 * speed);
                break;
            case ELASTIC_OUT:
                double c4 = (2 * Math.PI) / 3;
                factor = speed == 0 ? 0 : (speed == 1 ? 1 : (float)(Math.pow(2, -10 * speed) * Math.sin((speed * 10 - 0.75) * c4) + 1));
                break;
        }

        value += delta * factor;
    }

    /**
     * Sets the target value for animation.
     */
    public void setTarget(float target) {
        this.target = target;
    }

    /**
     * Gets the current animated value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value immediately without animation.
     */
    public void setValue(float value) {
        this.value = value;
        this.target = value;
    }

    /**
     * Checks if animation is complete.
     */
    public boolean isDone() {
        return Math.abs(value - target) < 0.001f;
    }

    /**
     * Gets the target value.
     */
    public float getTarget() {
        return target;
    }
}
