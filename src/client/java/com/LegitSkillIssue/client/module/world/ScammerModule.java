package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class ScammerModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 30.0, 5.0, 300.0, 5.0);
    private int ticks = 0;

    public ScammerModule() {
        super("Scammer", "Automatically sends scam messages.", Category.WORLD);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        ticks++;
        if (ticks >= delay.getValue() * 20) {
            mc.player.networkHandler.sendChatMessage("Join my discord for free hacks! LegitSkillIssue on top!");
            ticks = 0;
        }
    }
}
