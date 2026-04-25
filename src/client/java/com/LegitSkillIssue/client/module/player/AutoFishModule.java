package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.Hand;

public class AutoFishModule extends Module {
    private int delay = 0;

    public AutoFishModule() {
        super("AutoFish", "Automatically catches fish.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (delay > 0) {
            delay--;
            return;
        }

        if (mc.player.fishHook != null) {
            if (mc.player.fishHook.getVelocity().y < -0.02) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                delay = 20; 
            }
        }
    }
}
