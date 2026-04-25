package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import java.util.Random;

public class DerpModule extends Module {
    private final Random random = new Random();

    public DerpModule() {
        super("Derp", "Randomly rotates your head.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        
        mc.player.setYaw(random.nextFloat() * 360);
        mc.player.setPitch(random.nextFloat() * 180 - 90);
    }
}
