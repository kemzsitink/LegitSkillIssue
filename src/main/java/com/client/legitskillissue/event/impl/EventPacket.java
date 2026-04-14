package com.client.legitskillissue.event.impl;

import com.client.legitskillissue.event.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    private Packet<?> packet;
    public final boolean isSend;

    public EventPacket(Packet<?> packet, boolean isSend) {
        this.packet = packet;
        this.isSend = isSend;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
