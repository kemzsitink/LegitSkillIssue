package com.legitskillissue.client.gui;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.constraints.ConstantColorConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import java.awt.Color;

/**
 * Utility class to make Elementa animations more Java-friendly.
 */
public class ElementaUtils {
    public static void animateColor(UIComponent comp, Color target, float duration) {
        AnimatingConstraints anim = comp.makeAnimation();
        anim.setColorAnimation(Animations.OUT_EXP, duration, new ConstantColorConstraint(target));
        comp.animateTo(anim);
    }

    public static void animateHeight(UIComponent comp, float target, float duration) {
        AnimatingConstraints anim = comp.makeAnimation();
        anim.setHeightAnimation(Animations.OUT_EXP, duration, new PixelConstraint(target));
        comp.animateTo(anim);
    }

    public static void animateY(UIComponent comp, float target, float duration) {
        AnimatingConstraints anim = comp.makeAnimation();
        anim.setYAnimation(Animations.OUT_EXP, duration, new PixelConstraint(target));
        comp.animateTo(anim);
    }

    public static void animateX(UIComponent comp, float target, float duration) {
        AnimatingConstraints anim = comp.makeAnimation();
        anim.setXAnimation(Animations.OUT_EXP, duration, new PixelConstraint(target));
        comp.animateTo(anim);
    }
}
