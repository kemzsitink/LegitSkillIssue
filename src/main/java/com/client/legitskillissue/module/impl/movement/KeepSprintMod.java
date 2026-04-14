package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class KeepSprintMod extends Module {

    private boolean pendingResprint = false;

    public KeepSprintMod() { super("KeepSprint", Category.MOVEMENT); }

    @Override
    public void onTick() {
        if (!pendingResprint) return;
        pendingResprint = false;
        if (mc.thePlayer == null) return;
        mc.thePlayer.setSprinting(true);
    }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        if (!(packet instanceof C02PacketUseEntity)) return false;
        if (((C02PacketUseEntity) packet).getAction() != C02PacketUseEntity.Action.ATTACK) return false;
        pendingResprint = true;
        return false;
    }

    @Override
    public void onDisable() { pendingResprint = false; }
}
