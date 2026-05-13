package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class Derp2XMod extends Module {
    public Derp2XMod() {
        super("Derp2", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null) { /* General placeholder logic */ }
    }
}
