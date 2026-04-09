package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class BacktrackMod extends Module {
    private final Map<Integer, Queue<DelayedPacket>> packetMap = new ConcurrentHashMap<>();
    public int delayMs  = 150;
    private static final int MAX_QUEUE = 20;
    private Field s14EntityIdField;

    public BacktrackMod() {
        super("Backtrack");
        try {
            s14EntityIdField = S14PacketEntity.class.getDeclaredField("entityId");
        } catch (Exception e) {
            try {
                s14EntityIdField = S14PacketEntity.class.getDeclaredField("field_149072_a");
            } catch (Exception ex) {}
        }
        if (s14EntityIdField != null) {
            s14EntityIdField.setAccessible(true);
        }
    }

    private static class DelayedPacket {
        Packet<INetHandlerPlayClient> packet;
        long receiveTime;

        DelayedPacket(Packet<INetHandlerPlayClient> p, long t) {
            this.packet = p;
            this.receiveTime = t;
        }
    }

    @Override
    public void onTick() {
        if (mc.getNetHandler() == null) return;
        long now = System.currentTimeMillis();
        
        for (Integer entityId : packetMap.keySet()) {
            Queue<DelayedPacket> queue = packetMap.get(entityId);
            while (!queue.isEmpty() && now - queue.peek().receiveTime >= delayMs) {
                DelayedPacket dp = queue.poll();
                dp.packet.processPacket(mc.getNetHandler());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onPacketReceive(Packet<?> packet) {
        if (packet instanceof S14PacketEntity || packet instanceof S18PacketEntityTeleport) {
            int id = -1;
            try {
                if (packet instanceof S14PacketEntity && s14EntityIdField != null) {
                    id = s14EntityIdField.getInt(packet);
                } else if (packet instanceof S18PacketEntityTeleport) {
                    id = ((S18PacketEntityTeleport) packet).getEntityId();
                }
            } catch (Exception e) {}

            if (id != -1 && id != mc.thePlayer.getEntityId()) {
                Queue<DelayedPacket> q = packetMap.computeIfAbsent(id, k -> new LinkedList<>());
                if (q.size() < MAX_QUEUE) // drop if queue too full
                    q.add(new DelayedPacket((Packet<INetHandlerPlayClient>) packet, System.currentTimeMillis()));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        packetMap.clear();
    }
}
