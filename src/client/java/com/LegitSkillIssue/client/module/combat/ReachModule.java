package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class ReachModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.0, 3.0, 6.0, 0.1);

    public ReachModule() {
        super("Reach", "Increases your attack range.", Category.COMBAT);
        addSetting(range);
    }
}
