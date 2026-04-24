package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.player.PlayerEntity;

public class AntiBotModule extends Module {
    public AntiBotModule() {
        super("AntiBot", "Ignores anti-cheat bots.", Category.COMBAT);
    }

    public static boolean isBot(PlayerEntity player) {
        // Basic check: bots often are not in the player list (tab list)
        if (player.getAbilities().invulnerable && player.isInvisible()) return true; // Common bot flags
        
        // Check if player is in the tab list
        if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null) {
            return true;
        }
        return false;
    }
}
