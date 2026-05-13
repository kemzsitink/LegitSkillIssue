package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class AntiAimXMod extends Module {
    public AntiAimXMod() {
        super("AntiAim", Category.MISC);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null) { mc.thePlayer.rotationYawHead += 45; mc.thePlayer.renderYawOffset += 45; }
    }
}
