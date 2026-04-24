package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class HitBoxModule extends Module {
    public final NumberSetting expand = new NumberSetting("Expand", 0.1, 0.0, 1.0, 0.05);

    public HitBoxModule() {
        super("HitBox", "Expands entity hitboxes.", Category.COMBAT);
        addSetting(expand);
    }
}
