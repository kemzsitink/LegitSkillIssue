package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * REFACTORED (Senior): AutoBlink — Thread-Safe & Packet Ordering
 */
public class AutoBlinkMod extends Module {

    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private long lastToggleTime;
    private boolean isBlinking;

    public AutoBlinkMod() {
        super("AutoBlink", Category.EXPLOIT);
    }

    @Override
    protected void onEnable() {
        lastToggleTime = System.currentTimeMillis();
        isBlinking     = true;
        packets.clear();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        
        if (mc.thePlayer == null || mc.getNetHandler() == null) {
            packets.clear();
            return;
        }

        // Auto toggle every 3 seconds
        if (System.currentTimeMillis() - lastToggleTime >= 3000) {
            isBlinking = !isBlinking;
            lastToggleTime = System.currentTimeMillis();

            if (!isBlinking) {
                flushPackets();
            }
        }
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (mc.thePlayer == null || !isBlinking) return false;

        if (packet instanceof C03PacketPlayer
                || packet instanceof C02PacketUseEntity
                || packet instanceof C08PacketPlayerBlockPlacement
                || packet instanceof C07PacketPlayerDigging
                || packet instanceof C0BPacketEntityAction
                || packet instanceof C09PacketHeldItemChange
                || packet instanceof C0APacketAnimation) {
            packets.add(packet);
            return true; // Keep it, don't send
        }

        return false;
    }

    @Override
    public void onDisable() {
        flushPackets();
        isBlinking = false;
    }

    private void flushPackets() {
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
            packets.clear();
            return;
        }

        // ConcurrentLinkedQueue is thread-safe for polling
        while (!packets.isEmpty()) {
            Packet<?> p = packets.poll();
            if (p != null) {
                mc.getNetHandler().getNetworkManager().sendPacket(p);
            }
        }
    }
}
