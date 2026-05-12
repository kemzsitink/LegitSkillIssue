package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;

public class StepMod extends Module {

    public final NumberSetting height = addSetting(new NumberSetting("Height", "Step height", 1f, 10f, 0.5f, 1.5f));

    public StepMod() {
        super("Step", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = height.getValue();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = 0.6f; // Default vanilla step height
        }
    }
}
