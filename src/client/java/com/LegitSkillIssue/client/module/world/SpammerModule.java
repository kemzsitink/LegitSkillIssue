package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class SpammerModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 3.0, 1.0, 10.0, 1.0);
    private int ticks = 0;

    public SpammerModule() {
        super("Spammer", "Spams a message in chat.", Category.WORLD);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        ticks++;
        if (ticks >= delay.getValue() * 20) {
            mc.player.networkHandler.sendChatMessage("LegitSkillIssue is a modern utility mod for Fabric 1.21.4!");
            ticks = 0;
        }
    }
}
