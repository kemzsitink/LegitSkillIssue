package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class HitDelayFixMod extends Module {

    private static final Field LEFT_CLICK_COUNTER = ReflectionUtil.findField(
            Minecraft.class, "leftClickCounter", "field_71452_i");

    public HitDelayFixMod() { super("HitDelayFix"); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.currentScreen != null) return;
        ReflectionUtil.setInt(LEFT_CLICK_COUNTER, mc, 0);
    }
}
