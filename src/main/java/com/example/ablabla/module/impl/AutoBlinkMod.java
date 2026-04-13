package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoBlinkMod extends Module {

    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private long lastToggleTime;
    private boolean isBlinking;

    public AutoBlinkMod() {
        super("AutoBlink");
    }

    @Override
    protected void onEnable() {
        lastToggleTime = System.currentTimeMillis();
        isBlinking = true;
        packets.clear();
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.getNetHandler() == null) {
            packets.clear();
            return;
        }

        // Tự động bật/tắt (Blink) mỗi 3 giây (3000ms)
        if (System.currentTimeMillis() - lastToggleTime >= 3000) {
            isBlinking = !isBlinking;
            lastToggleTime = System.currentTimeMillis();
            
            // Nếu chuyển từ Trạng thái Giam gói tin (Blink) sang Thả ra (Off)
            if (!isBlinking) {
                flushPackets();
            }
        }
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (mc.thePlayer == null || !isBlinking) return false;

        // Bắt và giữ lại các gói tin quan trọng (Di chuyển, Tấn công, Đặt block, Hành động)
        if (packet instanceof C03PacketPlayer || 
            packet instanceof C02PacketUseEntity || 
            packet instanceof C08PacketPlayerBlockPlacement || 
            packet instanceof C07PacketPlayerDigging || 
            packet instanceof C0BPacketEntityAction || 
            packet instanceof C09PacketHeldItemChange ||
            packet instanceof C0APacketAnimation) {
            
            packets.add(packet);
            return true; // Hủy không cho gửi lên Server lúc này
        }

        return false; // Vẫn cho phép gửi các gói tin khác (KeepAlive, Chat...)
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
        
        while (!packets.isEmpty()) {
            Packet<?> p = packets.poll();
            if (p != null) {
                // Gửi trực tiếp qua NetworkManager để tránh bị Hook lại vào onPacketSend sinh ra vòng lặp vô hạn
                mc.getNetHandler().getNetworkManager().sendPacket(p);
            }
        }
    }
}
