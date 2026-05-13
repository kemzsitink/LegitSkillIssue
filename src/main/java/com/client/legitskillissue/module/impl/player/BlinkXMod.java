package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlinkXMod extends Module {

    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private EntityOtherPlayerMP clone;

    public BlinkXMod() {
        super("BlinkX", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        
        // Spawn clone to show original position
        clone = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        clone.copyLocationAndAnglesFrom(mc.thePlayer);
        clone.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-101, clone);
        
        packets.clear();
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer == null) return;
        
        // Send all buffered packets instantly to teleport
        while (!packets.isEmpty()) {
            if (mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() != null) {
                mc.getNetHandler().getNetworkManager().sendPacket(packets.poll());
            } else {
                packets.clear();
            }
        }
        
        // Remove clone
        if (clone != null && mc.theWorld != null) {
            mc.theWorld.removeEntityFromWorld(-101);
            clone = null;
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!event.isSend || mc.thePlayer == null) return;
        
        Packet<?> p = event.getPacket();
        if (p instanceof C03PacketPlayer || p instanceof C08PacketPlayerBlockPlacement || p instanceof C0APacketAnimation || p instanceof C0BPacketEntityAction || p instanceof C02PacketUseEntity) {
            packets.add(p);
            event.setCancelled(true);
        }
    }
}
