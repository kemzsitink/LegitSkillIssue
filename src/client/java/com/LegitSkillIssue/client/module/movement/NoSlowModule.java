package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.ModeSetting;

public class NoSlowModule extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Bypass");

    public NoSlowModule() {
        super("NoSlow", "Prevents slowdown while using items.", Category.MOVEMENT);
        addSetting(mode);
    }
}
