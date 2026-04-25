package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class FastBreakModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.0, 1.0, 5.0, 0.1);

    public FastBreakModule() {
        super("FastBreak", "Makes you break blocks faster.", Category.PLAYER);
        addSetting(speed);
    }
}
