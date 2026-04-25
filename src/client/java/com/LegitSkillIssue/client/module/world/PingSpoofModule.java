package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class PingSpoofModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 10.0);

    public PingSpoofModule() {
        super("PingSpoof", "Increases your ping on the server.", Category.WORLD);
        addSetting(delay);
    }
}
