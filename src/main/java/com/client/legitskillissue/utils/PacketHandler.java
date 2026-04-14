package com.client.legitskillissue.utils;

import com.client.legitskillissue.event.EventBus;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.ModuleManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketHandler extends ChannelDuplexHandler {
    public static final String HANDLER_ID = "legitskillissue_packet_handler";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet) {
            TpsTracker.INSTANCE.onPacketReceive((Packet<?>) msg);
            EventPacket event = new EventPacket((Packet<?>) msg, false);
            EventBus.INSTANCE.post(event);
            if (event.isCancelled() || ModuleManager.INSTANCE.onPacketReceive((Packet<?>) msg)) {
                return;
            }
            msg = event.getPacket(); // In case the packet was replaced
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Packet) {
            EventPacket event = new EventPacket((Packet<?>) msg, true);
            EventBus.INSTANCE.post(event);
            if (event.isCancelled() || ModuleManager.INSTANCE.onPacketSend((Packet<?>) msg)) {
                return;
            }
            msg = event.getPacket(); // In case the packet was replaced
        }
        super.write(ctx, msg, promise);
    }
    
    public static void inject() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) return;
        try {
            io.netty.channel.ChannelPipeline pipeline = mc.getNetHandler().getNetworkManager().channel().pipeline();
            if (pipeline.get(HANDLER_ID) != null) pipeline.remove(HANDLER_ID);
            pipeline.addBefore("packet_handler", HANDLER_ID, new PacketHandler());
        } catch (Exception ignored) {}
    }
    
    public static void remove() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            if (mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() != null) {
                io.netty.channel.Channel channel = mc.getNetHandler().getNetworkManager().channel();
                if (channel != null && channel.pipeline().get(HANDLER_ID) != null) {
                    channel.pipeline().remove(HANDLER_ID);
                }
            }
        } catch (Exception ignored) {}
    }
}
