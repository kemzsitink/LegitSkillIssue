package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class AutoSprintXMod extends Module {
    public AutoSprintXMod() {
        super("AutoSprint", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null && mc.thePlayer.movementInput.moveForward > 0) net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }
}
