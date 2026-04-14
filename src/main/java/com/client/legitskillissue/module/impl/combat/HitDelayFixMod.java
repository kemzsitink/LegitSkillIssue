package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class HitDelayFixMod extends Module {

    // MAPPING FIX (fields.csv):
    // field_71452_i = effectRenderer (SAI) — không phải leftClickCounter
    // Đúng theo fields.csv: field_71429_W = leftClickCounter
    private static final Field LEFT_CLICK_COUNTER = ReflectionUtil.findField(
            Minecraft.class, "leftClickCounter", "field_71429_W");

    public HitDelayFixMod() { super("HitDelayFix", Category.COMBAT); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.currentScreen != null) return;
        ReflectionUtil.setInt(LEFT_CLICK_COUNTER, mc, 0);
    }
}
