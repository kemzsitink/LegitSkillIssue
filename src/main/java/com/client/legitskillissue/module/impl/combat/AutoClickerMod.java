package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.utils.RandomUtils;
import com.client.legitskillissue.utils.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

/**
 * REFACTORED: AutoClicker with realistic human clicking patterns.
 * 
 * IMPROVEMENTS:
 * - Uses Gaussian distribution for click delays
 * - CPS stability: maintains consistent CPS during session (like humans)
 * - CPS drift: slowly changes CPS over time to mimic fatigue/warmup
 * - Only clicks on entities to avoid suspicious behavior
 */
public class AutoClickerMod extends Module {

    private long lastClick = 0;
    private double targetCps = 12.0;
    private long sessionStart = 0;
    
    public final NumberSetting minCps = addSetting(new NumberSetting("Min CPS", "Minimum clicks per second", 6f, 20f, 1f, 10f));
    public final NumberSetting maxCps = addSetting(new NumberSetting("Max CPS", "Maximum clicks per second", 6f, 30f, 1f, 14f));
    public final BooleanSetting cpsStability = addSetting(new BooleanSetting("CPS Stability", "Maintain consistent CPS like humans", true));

    public AutoClickerMod() {
        super("AutoClicker", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        sessionStart = System.currentTimeMillis();
        // Initialize target CPS with slight randomization
        double mean = (minCps.getValue() + maxCps.getValue()) / 2.0;
        targetCps = RandomUtils.gaussianRandomClamped(mean, 1.0, minCps.getValue(), maxCps.getValue());
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.currentScreen != null || mc.thePlayer == null) return;
            if (!Mouse.isButtonDown(0)) return;
    
            // Only fire when actually looking at an entity — never on block/miss
            MovingObjectPosition mop = mc.objectMouseOver;
            if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return;
    
            Entity target = mop.entityHit;
            if (target == null || target.isDead) return;
    
            long now = System.currentTimeMillis();
            
            // Calculate delay using Gaussian distribution
            long delay = RandomUtils.clickDelay(targetCps);
            
            if (now - lastClick < delay) return;
    
            lastClick = now;
            
            // CPS Stability: slowly drift CPS over time (mimic human fatigue/warmup)
            if (cpsStability.getValue()) {
                // Drift CPS every ~5 seconds
                if ((now - sessionStart) % 5000 < 50) {
                    double mean = (minCps.getValue() + maxCps.getValue()) / 2.0;
                    double drift = RandomUtils.gaussianRandom(0, 0.5); // Small drift
                    targetCps = Math.max(minCps.getValue(), Math.min(maxCps.getValue(), targetCps + drift));
                }
            } else {
                // No stability: randomize CPS each click (old behavior, more detectable)
                targetCps = minCps.getValue() + Math.random() * (maxCps.getValue() - minCps.getValue());
            }
    
            // Attack directly — no swingItem spam on miss/block
            mc.playerController.attackEntity(mc.thePlayer, target);
            mc.thePlayer.swingItem();
                }
    }
}
