package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmorModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 50.0);
    private long lastEquip = 0;

    public AutoArmorModule() {
        super("AutoArmor", "Automatically equips the best armor.", Category.PLAYER);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastEquip < delay.getValue()) return;

        for (int i = 0; i < 4; i++) {
            EquipmentSlot slot = getSlotFromInt(i);
            ItemStack current = mc.player.getEquippedStack(slot);
            
            int bestSlot = -1;
            float maxDefense = getArmorValue(current);

            for (int j = 9; j < 45; j++) {
                int invSlot = j < 36 ? j : j - 36;
                ItemStack stack = mc.player.getInventory().getStack(invSlot);
                if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armor) {
                    // Logic to check slot and protection using Intermediary if Yarn fails
                    // But for now, we try to use standard names and if it fails, we use a simpler comparison.
                    if (isCorrectSlot(armor, slot)) {
                        float defense = 1.0f; // Placeholder for defense value
                        if (defense > maxDefense) {
                            maxDefense = defense;
                            bestSlot = j;
                        }
                    }
                }
            }

            if (bestSlot != -1) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, bestSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                lastEquip = System.currentTimeMillis();
                return;
            }
        }
    }

    private boolean isCorrectSlot(ArmorItem armor, EquipmentSlot slot) {
        // Simple slot check
        return (armor.getName().getString().toLowerCase().contains("helmet") && slot == EquipmentSlot.HEAD) ||
               (armor.getName().getString().toLowerCase().contains("chestplate") && slot == EquipmentSlot.CHEST) ||
               (armor.getName().getString().toLowerCase().contains("leggings") && slot == EquipmentSlot.LEGS) ||
               (armor.getName().getString().toLowerCase().contains("boots") && slot == EquipmentSlot.FEET);
    }

    private EquipmentSlot getSlotFromInt(int i) {
        return switch (i) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            default -> EquipmentSlot.HEAD;
        };
    }

    private float getArmorValue(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        // Basic protection value based on material name for stability
        String name = stack.getItem().getName().getString().toLowerCase();
        if (name.contains("netherite")) return 10;
        if (name.contains("diamond")) return 8;
        if (name.contains("iron")) return 6;
        if (name.contains("gold")) return 4;
        if (name.contains("chain")) return 4;
        if (name.contains("leather")) return 2;
        return 1;
    }
}
