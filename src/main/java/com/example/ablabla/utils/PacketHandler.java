package com.example.ablabla.utils;

import com.example.ablabla.module.ModuleManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketHandler extends ChannelDuplexHandler {
    public static final String HANDLER_ID = "ablabla_packet_handler";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet) {
            TpsTracker.INSTANCE.onPacketReceive((Packet<?>) msg);
            if (ModuleManager.INSTANCE.onPacketReceive((Packet<?>) msg)) {
                System.out.println("[Ablabla-Logger] [IN] Cancelled Packet: " + msg.getClass().getSimpleName());
                return; // Cancel packet if a module requested it
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Packet) {
            if (ModuleManager.INSTANCE.onPacketSend((Packet<?>) msg)) {
                System.out.println("[Ablabla-Logger] [OUT] Cancelled Packet: " + msg.getClass().getSimpleName());
                return; // Cancel packet if a module requested it
            }
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
