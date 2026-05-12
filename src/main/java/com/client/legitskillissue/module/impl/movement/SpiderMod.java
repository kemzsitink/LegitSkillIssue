package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class SpiderMod extends Module {

    public SpiderMod() {
        super("Spider", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null) return;
        
        if (mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.motionY = 0.2;
        }
    }
}
