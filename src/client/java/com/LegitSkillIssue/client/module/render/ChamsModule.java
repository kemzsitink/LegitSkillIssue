package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.BooleanSetting;

public class ChamsModule extends Module {
    public final BooleanSetting colored = new BooleanSetting("Colored", true);
    public final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);

    public ChamsModule() {
        super("Chams", "Render entities through walls.", Category.RENDER);
        addSetting(colored);
        addSetting(throughWalls);
    }
}
