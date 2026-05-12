package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class AutoRespawnMod extends Module {

    public AutoRespawnMod() {
        super("AutoRespawn", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null && mc.thePlayer.isDead) {
            mc.thePlayer.respawnPlayer();
        }
    }
}
