package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import java.lang.reflect.Method;
import java.util.Random;

public class TriggerBotMod extends Module {

    public final NumberSetting minDelay = addSetting(new NumberSetting("Min Delay", "Min delay ms", 40f, 200f, 5f, 60f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("Max Delay", "Max delay ms", 40f, 300f, 5f, 120f));

    private static final Method CLICK_MOUSE = ReflectionUtil.findMethod(
            Minecraft.class, "clickMouse", "func_147116_af");

    private final Random rng = new Random();
    private long targetSince = -1;
    private long nextClickAt = -1;

    public TriggerBotMod() { super("TriggerBot"); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.currentScreen != null || CLICK_MOUSE == null) return;

        MovingObjectPosition mop = mc.objectMouseOver;
        boolean onTarget = mop != null
                && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && mop.entityHit instanceof EntityPlayer
                && mop.entityHit != mc.thePlayer
                && !mop.entityHit.isDead;

        if (!onTarget) { targetSince = -1; nextClickAt = -1; return; }

        long now = System.currentTimeMillis();
        if (targetSince == -1) {
            targetSince = now;
            nextClickAt = now + minDelay.getInt() + rng.nextInt(Math.max(1, maxDelay.getInt() - minDelay.getInt()));
            return;
        }
        if (now < nextClickAt) return;

        ReflectionUtil.invoke(CLICK_MOUSE, mc);
        targetSince = -1;
        nextClickAt = -1;
    }

    @Override
    public void onDisable() { targetSince = -1; nextClickAt = -1; }
}
