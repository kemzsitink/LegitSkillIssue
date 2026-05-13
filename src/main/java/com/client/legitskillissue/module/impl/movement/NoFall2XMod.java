package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class NoFall2XMod extends Module {
    public NoFall2XMod() {
        super("NoFall2", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer != null && mc.thePlayer.fallDistance > 2) mc.getNetHandler().addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer(true));
    }
}
