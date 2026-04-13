package com.example.ablabla.utils;

import io.netty.channel.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ARCHITECT NETWORK CONTROLLER
 * Manages two separate streams of traffic over the same channel.
 */
public class PriorityPacketHandler extends ChannelOutboundHandlerAdapter {
    public static final String HANDLER_ID = "ablabla_priority_handler";
    
    // Separate stream for stress packets
    private final ConcurrentLinkedQueue<Object> stressQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger stressCount = new AtomicInteger(0);
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // LUỒNG 1: CLIENT LANE (High Priority)
        // Các gói tin quan trọng hoặc gói tin từ hệ thống MC gốc sẽ đi qua lane này
        if (isHighPriority(msg)) {
            super.write(ctx, msg, promise);
            return;
        }

        // LUỒNG 2: STRESS LANE
        // Các gói tin tấn công được đẩy vào hàng đợi riêng
        if (msg.getClass().getName().startsWith("com.example.ablabla") || isStressPacket(msg)) {
            stressQueue.add(msg);
            stressCount.incrementAndGet();
            return;
        }

        // Mặc định cho các gói tin khác
        super.write(ctx, msg, promise);
    }

    private boolean isHighPriority(Object msg) {
        return msg instanceof C00PacketKeepAlive || msg instanceof C03PacketPlayer;
    }

    private boolean isStressPacket(Object msg) {
        // Có thể mở rộng logic nhận diện gói tin stress tại đây
        return false; 
    }

    /**
     * "Xả" luồng Stress Lane mà không làm nghẽn luồng chính.
     */
    public void flushStress(ChannelHandlerContext ctx, int batchSize) {
        if (ctx.channel().isOpen() && ctx.channel().isWritable()) {
            for (int i = 0; i < batchSize; i++) {
                Object msg = stressQueue.poll();
                if (msg == null) break;
                ctx.write(msg);
                stressCount.decrementAndGet();
            }
            ctx.flush();
        }
    }

    public int getPendingStress() {
        return stressCount.get();
    }

    public static void inject(Channel channel) {
        if (channel.pipeline().get(HANDLER_ID) == null) {
            channel.pipeline().addFirst(HANDLER_ID, new PriorityPacketHandler());
        }
    }
}
