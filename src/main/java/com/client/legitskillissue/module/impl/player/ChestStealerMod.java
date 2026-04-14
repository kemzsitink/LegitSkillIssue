package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;

public class ChestStealerMod extends Module {

    public final NumberSetting delay = addSetting(
            new NumberSetting("Steal Delay", "Ms between clicks", 80f, 300f, 10f, 120f));

    private long lastClick = 0;

    public ChestStealerMod() { super("ChestStealer", Category.PLAYER); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.playerController == null) return;
        if (!(mc.currentScreen instanceof GuiChest)) return;
        GuiChest gui = (GuiChest) mc.currentScreen;
        if (!(gui.inventorySlots instanceof ContainerChest)) return;

        long now = System.currentTimeMillis();
        if (now - lastClick < delay.getInt()) return;

        ContainerChest container = (ContainerChest) gui.inventorySlots;
        int size = container.getLowerChestInventory().getSizeInventory();
        for (int i = 0; i < size; i++) {
            Slot slot = container.getSlot(i);
            if (slot == null || !slot.getHasStack()) continue;
            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, 1, mc.thePlayer);
            lastClick = now;
            return;
        }
    }

    @Override
    public void onDisable() { lastClick = 0; }
}
