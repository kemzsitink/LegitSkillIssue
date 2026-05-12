package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class InventoryManagerMod extends Module {

    public final BooleanSetting autoArmor = addSetting(new BooleanSetting("AutoArmor", "Equip best armor", true));
    public final BooleanSetting cleaner = addSetting(new BooleanSetting("Cleaner", "Drop junk items", true));
    public final NumberSetting delay = addSetting(new NumberSetting("Delay", "Ticks between actions", 0f, 10f, 1f, 2f));

    private int tickDelay = 0;

    public InventoryManagerMod() {
        super("InventoryManager", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null) return;
        
        // Only run when inventory is open or if we want it to run silently in background
        // For bypasses, it's safer to only run when the inventory GUI is actually open, 
        // or send fake open/close packets. We'll run it only when GUI is null for "silent" 
        // or when GuiInventory is open. Let's do it silently with fake packets for now.
        
        if (mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)) {
            return;
        }

        tickDelay++;
        if (tickDelay < delay.getInt()) return;

        boolean didAction = false;

        if (autoArmor.getValue()) {
            didAction = equipBestArmor();
        }

        if (!didAction && cleaner.getValue()) {
            didAction = cleanJunk();
        }

        if (didAction) {
            tickDelay = 0;
        }
    }

    private boolean equipBestArmor() {
        for (int type = 0; type < 4; type++) {
            int bestArmorSlot = -1;
            float bestArmorValue = -1f;

            // Check current equipped armor
            ItemStack currentArmor = mc.thePlayer.inventory.armorItemInSlot(type);
            if (currentArmor != null && currentArmor.getItem() instanceof ItemArmor) {
                bestArmorValue = getArmorValue((ItemArmor) currentArmor.getItem(), currentArmor);
            }

            // Find better armor in inventory
            for (int i = 9; i < 45; i++) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    if (armor.armorType == 3 - type) { // 0=helmet, 1=chest, 2=legs, 3=boots
                        float val = getArmorValue(armor, stack);
                        if (val > bestArmorValue) {
                            bestArmorValue = val;
                            bestArmorSlot = i;
                        }
                    }
                }
            }

            // Equip the better armor
            if (bestArmorSlot != -1) {
                // If there's already armor equipped, drop it or move to inventory (dropping is easier for simple AutoArmor)
                boolean isInventoryOpen = mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory;
                
                if (!isInventoryOpen) {
                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }

                if (currentArmor != null) {
                    // Shift click current armor out
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 8 - type, 0, 1, mc.thePlayer);
                }
                
                // Shift click new armor in
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestArmorSlot, 0, 1, mc.thePlayer);
                
                if (!isInventoryOpen) {
                    mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
                }
                
                return true; // Action performed
            }
        }
        return false;
    }

    private boolean cleanJunk() {
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && isJunk(stack)) {
                
                boolean isInventoryOpen = mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory;
                if (!isInventoryOpen) {
                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }

                // Drop item (slot, button=1 to drop full stack, mode=4 for drop)
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                
                if (!isInventoryOpen) {
                    mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
                }
                return true;
            }
        }
        return false;
    }

    private float getArmorValue(ItemArmor armor, ItemStack stack) {
        // Simple value calculation: damage reduce amount + enchantments
        float val = armor.damageReduceAmount;
        int protection = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.protection.effectId, stack);
        val += protection * 0.5f; // Rough estimation
        return val;
    }

    private boolean isJunk(ItemStack stack) {
        // Simple junk detection
        String name = stack.getItem().getUnlocalizedName().toLowerCase();
        return name.contains("rotten_flesh") || name.contains("bone") || name.contains("spider_eye") 
            || name.contains("string") || name.contains("feather") || name.contains("seeds");
    }
}
