package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.utils.ReflectionUtil;
import com.client.legitskillissue.utils.TpsTracker;
import com.client.legitskillissue.utils.Constants;
import com.client.legitskillissue.utils.FieldCache;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S13PacketDestroyEntities;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * REFACTORED: Backtrack with TPS sync and memory leak fix.
 * 
 * IMPROVEMENTS:
 * - Syncs delay with server TPS instead of client time
 * - Cleans up queues when entities despawn (fixes memory leak)
 * - Uses FieldCache for better performance
 * - Limits max delay to prevent detection
 */
public class BacktrackMod extends Module {

    public final NumberSetting delayMs = addSetting(new NumberSetting("BT Delay", "Delay ms", 0f, 500f, 10f, 100f));
    public final BooleanSetting tpsSync = addSetting(new BooleanSetting("TPS Sync", "Sync with server TPS", true));
    
    private static final int MAX_QUEUE = Constants.MAX_BACKTRACK_QUEUE_SIZE;
    private final FieldCache fieldCache = FieldCache.getInstance();

    private final Map<Integer, Queue<DelayedPacket>> queues = new ConcurrentHashMap<>();
    private final Map<Integer, Long> lastSeenTime = new ConcurrentHashMap<>();

    public BacktrackMod() {
        super("Backtrack", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.getNetHandler() == null) return;
        
        long now = System.currentTimeMillis();
        long effectiveDelay = delayMs.getInt();
        
        // Sync with server TPS if enabled
        if (tpsSync.getValue()) {
            // Adjust delay based on server TPS
            // If server is lagging (TPS < 20), increase delay proportionally
            float tps = TpsTracker.INSTANCE.getTps();
            effectiveDelay = (long) (effectiveDelay * (20.0f / tps));
        }
        
        // Process delayed packets
        for (Queue<DelayedPacket> queue : queues.values()) {
            while (!queue.isEmpty() && now - queue.peek().time >= effectiveDelay) {
                DelayedPacket dp = queue.poll();
                if (dp != null) dp.packet.processPacket(mc.getNetHandler());
            }
        }
        
        // Cleanup: Remove queues for entities not seen in 5 seconds (memory leak fix)
        lastSeenTime.entrySet().removeIf(entry -> {
            if (now - entry.getValue() > 5000) {
                queues.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onPacketReceive(Packet<?> packet) {
        if (mc.thePlayer == null) return false;
        
        // Handle entity despawn packets (memory leak fix)
        if (packet instanceof S13PacketDestroyEntities) {
            int[] entityIds = ((S13PacketDestroyEntities) packet).getEntityIDs();
            for (int id : entityIds) {
                queues.remove(id);
                lastSeenTime.remove(id);
            }
            return false;
        }
        
        int id = -1;
        if (packet instanceof S14PacketEntity) {
            id = fieldCache.getInt(packet, S14PacketEntity.class, "entityId", "field_149074_a");
        } else if (packet instanceof S18PacketEntityTeleport) {
            id = ((S18PacketEntityTeleport) packet).getEntityId();
        }
        
        if (id == -1 || id == mc.thePlayer.getEntityId()) return false;

        // Update last seen time
        lastSeenTime.put(id, System.currentTimeMillis());
        
        Queue<DelayedPacket> q = queues.computeIfAbsent(id, k -> new ConcurrentLinkedQueue<>());
        if (q.size() >= MAX_QUEUE) q.poll();
        q.add(new DelayedPacket((Packet<INetHandlerPlayClient>) packet, System.currentTimeMillis()));
        return true;
    }

    @Override
    public void onDisable() {
        if (mc.getNetHandler() != null) {
            for (Queue<DelayedPacket> q : queues.values()) {
                while (!q.isEmpty()) {
                    q.poll().packet.processPacket(mc.getNetHandler());
                }
            }
        }
        queues.clear();
        lastSeenTime.clear();
    }

    private static final class DelayedPacket {
        final Packet<INetHandlerPlayClient> packet;
        final long time;
        DelayedPacket(Packet<INetHandlerPlayClient> p, long t) { packet = p; time = t; }
    }
}
