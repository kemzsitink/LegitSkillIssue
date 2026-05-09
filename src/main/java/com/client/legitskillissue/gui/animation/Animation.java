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
        EASE_IN_OUT_QUAD,
        EASE_OUT_CUBIC,
        EASE_OUT_EXPO
    }

    public Animation(float initialValue, float speed, EasingFunction easing) {
        this.value = initialValue;
        this.target = initialValue;
        this.speed = speed;
        this.easing = easing;
    }

    public Animation(float initialValue, float speed) {
        this(initialValue, speed, EasingFunction.EASE_OUT_CUBIC);
    }

    /**
     * Updates the animation value towards the target.
     * Call this every frame.
     */
    public void update() {
        if (Math.abs(value - target) < 0.001f) {
            value = target;
            return;
        }

        float delta = target - value;
        float step;

        switch (easing) {
            case LINEAR:
                step = delta * speed;
                break;
            case EASE_IN_OUT_QUAD:
                float t = speed;
                step = delta * (t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t);
                break;
            case EASE_OUT_CUBIC:
                float t2 = 1 - speed;
                step = delta * (1 - t2 * t2 * t2);
                break;
            case EASE_OUT_EXPO:
                step = delta * (1 - (float) Math.pow(2, -10 * speed));
                break;
            default:
                step = delta * speed;
        }

        value += step;
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
