package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class SneakMod extends Module {

    public SneakMod() {
        super("Sneak", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.isSend && event.getPacket() instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction action = (C0BPacketEntityAction) event.getPacket();
            if (action.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
                event.setCancelled(true);
            }
        }
    }
}
