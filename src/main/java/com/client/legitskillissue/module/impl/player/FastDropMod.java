package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Random;

/**
 * FAST DROP: Rapidly drop items from inventory or hotbar.
 * 
 * FIX: Added human-like delay to FullInv mode to prevent server kicks.
 */
public class FastDropMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Drop mode", "Single", "Burst", "FullInv"));
    public final NumberSetting packets = addSetting(new NumberSetting("Packets", "Packets per drop", 1, 64, 1, 10));
    public final NumberSetting delay = addSetting(new NumberSetting("Delay", "MS between items", 10, 500, 10, 50));
    public final BooleanSetting hotbarOnly = addSetting(new BooleanSetting("Hotbar Only", "Only drop hotbar", true));

    private long nextDropTime = 0;
    private final Random random = new Random();

    public FastDropMod() {
        super("FastDrop", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.currentScreen != null) return;

        if (mode.getMode().equalsIgnoreCase("FullInv")) {
            if (System.currentTimeMillis() < nextDropTime) return;

            int start = hotbarOnly.getValue() ? 36 : 9;
            int end = 45;
            
            boolean dropped = false;
            for (int i = start; i < end; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    dropItem(i, true);
                    dropped = true;
                    nextDropTime = System.currentTimeMillis() + (long)(delay.getValue() + random.nextInt(20));
                    break;
                }
            }
            
            if (!dropped) {
                this.toggle(); // Inventory empty
            }
        }
    }

    public void onDropKey() {
        if (!isEnabled()) return;

        String currentMode = mode.getMode();
        int burstSize = currentMode.equalsIgnoreCase("Burst") ? (int)packets.getValue() : 1;

        for (int i = 0; i < burstSize; i++) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.DROP_ALL_ITEMS, 
                    BlockPos.ORIGIN, 
                    EnumFacing.DOWN
            ));
        }
    }

    private void dropItem(int slot, boolean all) {
        mc.playerController.windowClick(0, slot, all ? 1 : 0, 4, mc.thePlayer);
    }
}
