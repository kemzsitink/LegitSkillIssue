package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawnModule extends Module {
    public AutoRespawnModule() {
        super("AutoRespawn", "Automatically respawns when you die.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}
