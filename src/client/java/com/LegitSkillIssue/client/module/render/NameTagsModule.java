package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.BooleanSetting;

public class NameTagsModule extends Module {
    public final BooleanSetting health = new BooleanSetting("Health", true);

    public NameTagsModule() {
        super("NameTags", "Better nametags for players.", Category.RENDER);
        addSetting(health);
    }
}
