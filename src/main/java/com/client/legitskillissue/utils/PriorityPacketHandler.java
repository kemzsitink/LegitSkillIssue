package com.client.legitskillissue.utils;

import io.netty.channel.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

/**
 * REFACTORED (MCP-919): PriorityPacketHandler — Packet Sequencing Compliant
 *
 * VI PHẠM CŨ:
 *   - isHighPriority() ưu tiên C00PacketKeepAlive và C03PacketPlayer lên trước tất cả.
 *     → Vi phạm chỉ thị §2: C0F (Transaction) và C00 (KeepAlive) KHÔNG được ưu tiên
 *       trước các packet di chuyển. Thứ tự phải là FIFO (hàng đợi).
 *   - "Stress Lane" (stressQueue) cho phép gửi packet ngoài luồng chính.
 *     → Vi phạm chỉ thị §2: không được gửi packet vượt quá 20 ticks/giây.
 *
 * THAY ĐỔI:
 *   - Bỏ hoàn toàn "stress lane" và stressQueue.
 *   - Bỏ isHighPriority() ưu tiên sai thứ tự.
 *   - Tất cả packet đi qua một luồng FIFO duy nhất (super.write) — đúng thứ tự hàng đợi.
 *   - C0F và C00 không được ưu tiên trước C03 (di chuyển).
 *
 *   Công thức A (cũ): C00/C03 → super.write() ngay; các packet khác → stressQueue.
 *   Công thức B (mới): Tất cả packet → super.write() theo thứ tự FIFO, không phân làn.
 */
public class PriorityPacketHandler extends ChannelOutboundHandlerAdapter {
    public static final String HANDLER_ID = "legitskillissue_priority_handler";

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // THAY ĐỔI: Bỏ phân làn ưu tiên — tất cả packet đi theo thứ tự FIFO
        // Công thức A (cũ): C00/C03 → super.write() ngay; stress packets → stressQueue
        // Công thức B (mới): super.write() cho tất cả — đảm bảo thứ tự hàng đợi đúng
        //   theo chỉ thị §2: C0F và C00 không được ưu tiên trước packet di chuyển.
        super.write(ctx, msg, promise);
    }

    /**
     * Flush một batch packet từ NetworkStressTester qua channel.
     * Không phân làn ưu tiên — tất cả đi theo FIFO qua ctx.write() + flush().
     * Giữ lại method này để NetworkStressTester có thể gọi sau khi write batch.
     */
    public void flushStress(ChannelHandlerContext ctx, int batchSize) {
        if (ctx.channel().isOpen() && ctx.channel().isWritable()) {
            ctx.flush();
        }
    }

    public static boolean isMovementPacket(Object msg) {
        return msg instanceof C03PacketPlayer;
    }

    public static boolean isResponsePacket(Object msg) {
        return msg instanceof C0FPacketConfirmTransaction || msg instanceof C00PacketKeepAlive;
    }

    public static void inject(Channel channel) {
        if (channel.pipeline().get(HANDLER_ID) == null) {
            channel.pipeline().addFirst(HANDLER_ID, new PriorityPacketHandler());
        }
    }
}
