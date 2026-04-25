package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.ModeSetting;
import net.minecraft.entity.player.PlayerEntity;

public class AntiBotModule extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Tab", "Tab", "ID", "Combined");

    public AntiBotModule() {
        super("AntiBot", "Detects and ignores anti-cheat bots.", Category.COMBAT);
        addSetting(mode);
    }

    public static boolean isBot(PlayerEntity player) {
        AntiBotModule module = (AntiBotModule) com.LegitSkillIssue.client.module.ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AntiBotModule)
                .findFirst().orElse(null);

        if (module == null || !module.isEnabled()) return false;

        String m = module.mode.getValue();

        // Tab List Check
        if (m.equals("Tab") || m.equals("Combined")) {
            if (mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null) {
                return true;
            }
        }

        // Entity ID Check (Bots often have very high IDs)
        if (m.equals("ID") || m.equals("Combined")) {
            if (player.getId() >= 1000000) return true;
        }

        // Common bot flags
        if (player.getAbilities().invulnerable && player.isInvisible() && player.getName().getString().length() > 16) {
            return true;
        }

        return false;
    }
}
