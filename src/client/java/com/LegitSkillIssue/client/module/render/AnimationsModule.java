package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class AnimationsModule extends Module {
    public final NumberSetting swingSpeed = new NumberSetting("Swing Speed", 1.0, 0.1, 5.0, 0.1);

    public AnimationsModule() {
        super("Animations", "Customizes view animations.", Category.RENDER);
        addSetting(swingSpeed);
    }
}
