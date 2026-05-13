package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class HeadRollXMod extends Module {
    public HeadRollXMod() {
        super("HeadRoll", Category.MISC);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null) mc.thePlayer.rotationPitch = (float) (Math.sin(System.currentTimeMillis() / 100.0) * 90);
    }
}
