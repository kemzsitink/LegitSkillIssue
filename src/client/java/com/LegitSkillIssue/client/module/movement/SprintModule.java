package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class SprintModule extends Module {
    public SprintModule() {
        super("Sprint", "Automatically sprints for you.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player != null && !mc.player.isSprinting() && mc.player.input.movementForward > 0) {
            mc.player.setSprinting(true);
        }
    }
}
