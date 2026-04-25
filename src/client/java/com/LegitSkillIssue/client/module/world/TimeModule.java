package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class TimeModule extends Module {
    public final NumberSetting time = new NumberSetting("Time", 12000.0, 0.0, 24000.0, 1000.0);

    public TimeModule() {
        super("Time", "Changes the client-side time.", Category.WORLD);
        addSetting(time);
    }

    @Override
    public void onTick() {
        if (mc.world != null) {
            // No direct setter in ClientWorld for 1.21 without Mixin usually.
            // But we can try setTime if it's available in the mapping.
        }
    }
}
