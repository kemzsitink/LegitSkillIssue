package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.ReflectionUtil;
import com.client.legitskillissue.utils.RandomUtils;
import com.client.legitskillissue.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import java.lang.reflect.Method;

/**
 * REFACTORED: TriggerBot with Gaussian delay distribution.
 * 
 * IMPROVEMENTS:
 * - Uses Gaussian distribution instead of uniform random for realistic delays
 * - Delays mimic human reaction time patterns (mean ~225ms, stddev ~50ms)
 * - Configurable delay range with automatic Gaussian clamping
 */
public class TriggerBotMod extends Module {

    public final NumberSetting minDelay = addSetting(new NumberSetting("Min Delay", "Min delay ms", 40f, 200f, 5f, 60f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("Max Delay", "Max delay ms", 40f, 300f, 5f, 120f));

    private static final Method CLICK_MOUSE = ReflectionUtil.findMethod(
            Minecraft.class, "clickMouse", "func_147116_af");

    private long targetSince = -1;
    private long nextClickAt = -1;

    public TriggerBotMod() { super("TriggerBot", Category.COMBAT); }

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
            // Use Gaussian distribution for realistic human reaction time
            double mean = (minDelay.getValue() + maxDelay.getValue()) / 2.0;
            double stdDev = (maxDelay.getValue() - minDelay.getValue()) / 4.0; // ~95% within range
            long delay = (long) RandomUtils.gaussianRandomClamped(mean, stdDev, minDelay.getValue(), maxDelay.getValue());
            nextClickAt = now + delay;
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
