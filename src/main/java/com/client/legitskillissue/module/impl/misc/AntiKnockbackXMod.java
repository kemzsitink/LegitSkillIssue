package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class AntiKnockbackXMod extends Module {
    public AntiKnockbackXMod() {
        super("AntiKnockback", Category.MISC);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null) { /* General placeholder logic */ }
    }
}
