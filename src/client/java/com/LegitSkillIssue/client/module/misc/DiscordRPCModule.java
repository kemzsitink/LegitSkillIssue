package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.text.Text;

public class DiscordRPCModule extends Module {
    public DiscordRPCModule() {
        super("DiscordRPC", "Shows your status on Discord.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§9[Discord] §fRich Presence enabled."), false);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal("§9[Discord] §fRich Presence disabled."), false);
        }
    }
}
