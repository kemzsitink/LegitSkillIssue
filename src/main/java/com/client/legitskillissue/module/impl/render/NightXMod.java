package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class NightXMod extends Module {
    public NightXMod() {
        super("Night", Category.RENDER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.theWorld != null) mc.theWorld.setWorldTime(18000);
    }
}
