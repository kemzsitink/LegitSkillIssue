package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.ReflectionUtil;
import com.client.legitskillissue.utils.RandomUtils;
import com.client.legitskillissue.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * REFACTORED: BlockHit with realistic timing delays.
 * 
 * IMPROVEMENTS:
 * - Adds random delay (5-15ms) between attack and block
 * - Uses separate thread pool to avoid blocking game thread
 * - Mimics human reaction time for block timing
 */
public class BlockHitMod extends Module {

    private static final Method RIGHT_CLICK = ReflectionUtil.findMethod(
            Minecraft.class, "rightClickMouse", "func_147121_ag");
    
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BlockHitMod() { super("BlockHit", Category.COMBAT); }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        if (!(packet instanceof C02PacketUseEntity) || RIGHT_CLICK == null) return false;
        if (((C02PacketUseEntity) packet).getAction() != C02PacketUseEntity.Action.ATTACK) return false;
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null) return false;
        if (!(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemAxe)) return false;
        
        // Add realistic delay between attack and block (5-15ms)
        long delay = Constants.BLOCKHIT_MIN_DELAY_MS + 
                     RandomUtils.getRandom().nextInt(Constants.BLOCKHIT_MAX_DELAY_MS - Constants.BLOCKHIT_MIN_DELAY_MS + 1);
        
        scheduler.schedule(() -> {
            mc.addScheduledTask(() -> ReflectionUtil.invoke(RIGHT_CLICK, mc));
        }, delay, TimeUnit.MILLISECONDS);
        
        return false;
    }
    
    @Override
    protected void onDisable() {
        // Clean up any pending tasks
        scheduler.shutdownNow();
    }
}
