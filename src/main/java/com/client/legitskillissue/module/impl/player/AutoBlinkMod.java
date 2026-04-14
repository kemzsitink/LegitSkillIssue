package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * REFACTORED (MCP-919): AutoBlink — Packet Queue Ordering Compliant
 *
 * VI PHẠM CŨ:
 *   - flushPackets() dùng ConcurrentLinkedQueue.poll() — thứ tự FIFO nhưng không phân loại.
 *     Nếu C0F/C00 nằm trước C03 trong queue (do thứ tự nhận), chúng sẽ được gửi trước
 *     packet di chuyển, vi phạm chỉ thị §2.
 *
 * THAY ĐỔI:
 *   - Dùng ArrayDeque (FIFO) thay vì ConcurrentLinkedQueue.
 *   - flushPackets() gửi theo 2 pass:
 *     Pass 1: Gửi tất cả packet di chuyển (C03 và subclass) trước.
 *     Pass 2: Gửi C0F (Transaction) và C00 (KeepAlive) sau.
 *     → Đảm bảo thứ tự đúng theo chỉ thị §2.
 *
 *   Công thức A (cũ): Flush theo thứ tự FIFO không phân loại.
 *   Công thức B (mới): Flush movement packets trước, response packets sau.
 */
public class AutoBlinkMod extends Module {

    private final Deque<Packet<?>> packets = new ArrayDeque<>();
    private long    lastToggleTime;
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

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.getNetHandler() == null) {
            packets.clear();
            return;
        }

        // Tự động bật/tắt mỗi 3 giây
        if (System.currentTimeMillis() - lastToggleTime >= 3000) {
            isBlinking     = !isBlinking;
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
            packets.addLast(packet);
            return true; // Giữ lại, chưa gửi
        }

        return false;
    }

    @Override
    public void onDisable() {
        flushPackets();
        isBlinking = false;
    }

    /**
     * Flush theo thứ tự ưu tiên đúng theo chỉ thị §2:
     * Pass 1 — Packet di chuyển (C03 và subclass) trước.
     * Pass 2 — Packet phản hồi (C0F Transaction, C00 KeepAlive) sau.
     * Pass 3 — Các packet còn lại.
     *
     * THAY ĐỔI tại đây:
     * Công thức A (cũ): while (!packets.isEmpty()) { sendPacket(packets.poll()); }
     * Công thức B (mới): 3-pass flush — movement → response → others
     */
    private void flushPackets() {
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
            packets.clear();
            return;
        }

        Deque<Packet<?>> movement  = new ArrayDeque<>();
        Deque<Packet<?>> response  = new ArrayDeque<>();
        Deque<Packet<?>> others    = new ArrayDeque<>();

        for (Packet<?> p : packets) {
            if (p instanceof C03PacketPlayer) {
                movement.addLast(p);
            } else if (p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive) {
                response.addLast(p);
            } else {
                others.addLast(p);
            }
        }
        packets.clear();

        // Pass 1: Di chuyển
        for (Packet<?> p : movement) {
            mc.getNetHandler().getNetworkManager().sendPacket(p);
        }
        // Pass 2: Phản hồi server (Transaction, KeepAlive)
        for (Packet<?> p : response) {
            mc.getNetHandler().getNetworkManager().sendPacket(p);
        }
        // Pass 3: Còn lại
        for (Packet<?> p : others) {
            mc.getNetHandler().getNetworkManager().sendPacket(p);
        }
    }
}
