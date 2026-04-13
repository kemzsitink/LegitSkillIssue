package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import com.example.ablabla.utils.ReflectionUtil;
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

    public final NumberSetting delayMs = addSetting(new NumberSetting("BT Delay", "Delay ms", 0f, 500f, 10f, 100f));
    private static final int MAX_QUEUE = 30;

    private static final Field S14_ENTITY_ID = ReflectionUtil.findField(
            S14PacketEntity.class, "entityId", "field_149072_a");

    private final Map<Integer, Queue<DelayedPacket>> queues = new ConcurrentHashMap<>();

    public BacktrackMod() {
        super("Backtrack");
    }

    @Override
    public void onTick() {
        if (mc.getNetHandler() == null) return;
        long now = System.currentTimeMillis();
        for (Queue<DelayedPacket> queue : queues.values()) {
            while (!queue.isEmpty() && now - queue.peek().time >= delayMs.getInt()) {
                queue.poll().packet.processPacket(mc.getNetHandler());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onPacketReceive(Packet<?> packet) {
        if (mc.thePlayer == null) return false;
        int id = -1;
        if (packet instanceof S14PacketEntity)
            id = ReflectionUtil.getInt(S14_ENTITY_ID, packet, -1);
        else if (packet instanceof S18PacketEntityTeleport)
            id = ((S18PacketEntityTeleport) packet).getEntityId();
        if (id == -1 || id == mc.thePlayer.getEntityId()) return false;

        Queue<DelayedPacket> q = queues.computeIfAbsent(id, k -> new LinkedList<>());
        if (q.size() >= MAX_QUEUE) q.poll();
        q.add(new DelayedPacket((Packet<INetHandlerPlayClient>) packet, System.currentTimeMillis()));
        return true;
    }

    @Override
    public void onDisable() {
        if (mc.getNetHandler() != null)
            for (Queue<DelayedPacket> q : queues.values())
                while (!q.isEmpty()) q.poll().packet.processPacket(mc.getNetHandler());
        queues.clear();
    }

    private static final class DelayedPacket {
        final Packet<INetHandlerPlayClient> packet;
        final long time;
        DelayedPacket(Packet<INetHandlerPlayClient> p, long t) { packet = p; time = t; }
    }
}
