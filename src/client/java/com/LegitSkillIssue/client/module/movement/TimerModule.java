package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class TimerModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 10.0, 0.1);

    public TimerModule() {
        super("Timer", "Changes game speed.", Category.MOVEMENT);
        addSetting(speed);
    }
}
