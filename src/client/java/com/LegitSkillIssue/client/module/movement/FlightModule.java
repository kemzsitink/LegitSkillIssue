package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class FlightModule extends Module {
    public FlightModule() {
        super("Flight", "Allows you to fly like in creative mode.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                mc.player.getAbilities().flying = false;
            }
        }
    }
}
