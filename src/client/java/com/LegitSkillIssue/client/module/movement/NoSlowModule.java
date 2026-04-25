package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.BooleanSetting;

public class NoSlowModule extends Module {
    public final BooleanSetting items = new BooleanSetting("Items", true);
    public final BooleanSetting webs = new BooleanSetting("Webs", true);
    public final BooleanSetting soulSand = new BooleanSetting("Nether", true);

    public NoSlowModule() {
        super("NoSlow", "Prevents various slowdown effects.", Category.MOVEMENT);
        addSetting(items);
        addSetting(webs);
        addSetting(soulSand);
    }
}
