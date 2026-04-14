package com.example.ablabla.module.impl.player;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class FastPlaceMod extends Module {

    private static final Field DELAY_TIMER = ReflectionUtil.findField(
            Minecraft.class, "rightClickDelayTimer", "field_71467_ac");

    public FastPlaceMod() {
        super("FastPlace");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;
        ReflectionUtil.setInt(DELAY_TIMER, mc, 0);
    }

    @Override
    public void onDisable() {
        ReflectionUtil.setInt(DELAY_TIMER, mc, 4);
    }
}
