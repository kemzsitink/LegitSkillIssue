package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class AutoJumpXMod extends Module {
    public AutoJumpXMod() {
        super("AutoJump", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null && mc.thePlayer.onGround) mc.thePlayer.jump();
    }
}
