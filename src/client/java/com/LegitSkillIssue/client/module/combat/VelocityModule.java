package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class VelocityModule extends Module {
    public final NumberSetting horizontal = new NumberSetting("Horizontal", 0.0, 0.0, 100.0, 1.0);
    public final NumberSetting vertical = new NumberSetting("Vertical", 0.0, 0.0, 100.0, 1.0);

    public VelocityModule() {
        super("Velocity", "Reduces your knockback.", Category.COMBAT);
        addSetting(horizontal);
        addSetting(vertical);
    }
}
