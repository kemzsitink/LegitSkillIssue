package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;

import java.util.Random;

/**
 * REFACTORED: ChestStealer (Legit)
 * 
 * Improvements:
 * - Smart delay with jitter: Simulates human reaction times.
 * - Auto Close: Automatically closes the chest when empty.
 * - Legit Mode: Ignores specific trash items (Optional logic expansion).
 */
public class ChestStealerMod extends Module {

    public final NumberSetting minDelay = addSetting(new NumberSetting("Min Delay", "Minimum click delay", 50f, 500f, 10f, 100f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("Max Delay", "Maximum click delay", 50f, 500f, 10f, 150f));
    public final BooleanSetting autoClose = addSetting(new BooleanSetting("Auto Close", "Close chest when empty", true));

    private long nextClickTime = 0;
    private final Random random = new Random();

    public ChestStealerMod() {
        super("ChestStealer", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.thePlayer == null || mc.playerController == null) return;
            if (!(mc.currentScreen instanceof GuiChest)) return;
    
            GuiChest gui = (GuiChest) mc.currentScreen;
            ContainerChest container = (ContainerChest) gui.inventorySlots;
    
            if (System.currentTimeMillis() < nextClickTime) return;
    
            int size = container.getLowerChestInventory().getSizeInventory();
            boolean isEmpty = true;
    
            for (int i = 0; i < size; i++) {
                Slot slot = container.getSlot(i);
                if (slot.getHasStack()) {
                    isEmpty = false;
                    mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, 1, mc.thePlayer);
                    
                    // Set next randomized delay
                    long delay = (long) (minDelay.getValue() + random.nextDouble() * (maxDelay.getValue() - minDelay.getValue()));
                    nextClickTime = System.currentTimeMillis() + delay;
                    return;
                }
            }
    
            if (isEmpty && autoClose.getValue()) {
                mc.thePlayer.closeScreen();
            }
                }
    }

    @Override
    protected void onEnable() {
        nextClickTime = 0;
    }
}
