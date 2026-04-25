package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.attribute.EntityAttributes;

public class StepModule extends Module {
    public final NumberSetting height = new NumberSetting("Height", 1.0, 0.6, 2.5, 0.1);

    public StepModule() {
        super("Step", "Allows you to step up blocks.", Category.MOVEMENT);
        addSetting(height);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        var instance = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (instance != null) {
            instance.setBaseValue(height.getValue());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            var instance = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
            if (instance != null) {
                instance.setBaseValue(0.6);
            }
        }
    }
}
