package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C00PacketKeepAlive;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeLagXMod extends Module {

    public final NumberSetting maxPackets = addSetting(new NumberSetting("Max Packets", "Packets to buffer", 10f, 100f, 1f, 20f));
    
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private boolean sending = false;

    public FakeLagXMod() {
        super("FakeLagX", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null) return;
        
        if (packetQueue.size() >= maxPackets.getInt()) {
            sending = true;
            while (!packetQueue.isEmpty()) {
                mc.getNetHandler().getNetworkManager().sendPacket(packetQueue.poll());
            }
            sending = false;
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!event.isSend || sending || mc.thePlayer == null) return;
        
        Packet<?> packet = event.getPacket();
        if (packet instanceof C03PacketPlayer || packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
            packetQueue.add(packet);
            event.setCancelled(true);
        }
    }

    @Override
    protected void onDisable() {
        sending = true;
        while (!packetQueue.isEmpty()) {
            if (mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() != null) {
                mc.getNetHandler().getNetworkManager().sendPacket(packetQueue.poll());
            } else {
                packetQueue.clear();
            }
        }
        sending = false;
    }
}
