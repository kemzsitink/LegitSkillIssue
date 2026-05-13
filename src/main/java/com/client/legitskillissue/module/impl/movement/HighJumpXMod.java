package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class HighJumpXMod extends Module {
    public HighJumpXMod() {
        super("HighJump", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null && mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY = 1.5;
    }
}
