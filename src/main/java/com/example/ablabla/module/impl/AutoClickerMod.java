package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class AutoClickerMod extends Module {

    private long lastClick = 0;
    private double currentCps = 12.0;
    public final NumberSetting minCps = addSetting(new NumberSetting("Min CPS", "Minimum clicks per second", 6f, 20f, 1f, 10f));
    public final NumberSetting maxCps = addSetting(new NumberSetting("Max CPS", "Maximum clicks per second", 6f, 30f, 1f, 14f));

    public AutoClickerMod() {
        super("AutoClicker");
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null || mc.thePlayer == null) return;
        if (!Mouse.isButtonDown(0)) return;

        // Only fire when actually looking at an entity — never on block/miss
        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return;

        Entity target = mop.entityHit;
        if (target == null || target.isDead) return;

        long now = System.currentTimeMillis();
        if (now - lastClick < 1000.0 / currentCps) return;

        lastClick = now;
        currentCps = minCps.getValue() + Math.random() * (maxCps.getValue() - minCps.getValue());

        // Attack directly — no swingItem spam on miss/block
        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();
    }
}
