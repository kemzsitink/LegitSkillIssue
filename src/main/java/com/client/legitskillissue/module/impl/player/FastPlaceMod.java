package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class FastPlaceMod extends Module {

    private static final Field DELAY_TIMER = ReflectionUtil.findField(
            Minecraft.class, "rightClickDelayTimer", "field_71467_ac");

    public FastPlaceMod() {
        super("FastPlace", Category.PLAYER);
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
