package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class FastPlaceModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 0.0, 0.0, 4.0, 1.0);

    public FastPlaceModule() {
        super("FastPlace", "Removes block placing delay.", Category.PLAYER);
        addSetting(delay);
    }
}
