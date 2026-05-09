package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RandomUtils;
import com.client.legitskillissue.utils.Logger;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * AutoArmor - Automatically equips best armor with human-like delays.
 * 
 * FEATURES:
 * - Smart armor selection (Best/Balanced/Durability modes)
 * - Human-like delays with Gaussian distribution
 * - Durability threshold for auto-replacement
 * - Hotbar-only mode for legit gameplay
 * - Open inventory option
 * 
 * ANTI-CHEAT SAFE:
 * - Realistic delays (100-500ms)
 * - Only works when inventory is open (optional)
 * - Respects server tick rate
 */
public class AutoArmorMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Armor selection mode", 
        "Best", "Balanced", "Durability"));
    public final NumberSetting minDelay = addSetting(new NumberSetting("Min Delay", "Min delay ms", 50f, 500f, 10f, 100f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("Max Delay", "Max delay ms", 100f, 1000f, 10f, 300f));
    public final NumberSetting durabilityThreshold = addSetting(new NumberSetting("Durability", "Replace when below %", 5f, 100f, 5f, 20f));
    public final BooleanSetting openInv = addSetting(new BooleanSetting("Open Inv", "Only work when inventory open", true));
    public final BooleanSetting hotbarOnly = addSetting(new BooleanSetting("Hotbar Only", "Only use armor from hotbar", false));

    private static final Logger logger = Logger.getLogger(AutoArmorMod.class);
    
    private long lastEquipTime = 0;
    private int currentSlot = -1;
    private boolean isEquipping = false;

    // Armor slots: 0=boots, 1=leggings, 2=chestplate, 3=helmet
    private static final int[] ARMOR_SLOTS = {36, 37, 38, 39}; // Inventory slots for armor

    public AutoArmorMod() {
        super("AutoArmor", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.thePlayer.capabilities.isCreativeMode) return;
        
        // Check if inventory should be open
        if (openInv.getValue() && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }

        // Delay between equips
        long now = System.currentTimeMillis();
        if (isEquipping && now - lastEquipTime < getRandomDelay()) {
            return;
        }

        // Check each armor slot
        for (int armorSlot = 0; armorSlot < 4; armorSlot++) {
            ItemStack currentArmor = mc.thePlayer.inventory.armorInventory[armorSlot];
            
            // Check if current armor needs replacement
            if (shouldReplaceArmor(currentArmor, armorSlot)) {
                ItemStack bestArmor = findBestArmor(armorSlot);
                
                if (bestArmor != null) {
                    int bestSlot = getArmorSlot(bestArmor, armorSlot);
                    if (bestSlot != -1) {
                        equipArmor(bestSlot, armorSlot);
                        return; // Only equip one piece per tick
                    }
                }
            }
        }
        
        isEquipping = false;
    }

    /**
     * Checks if current armor should be replaced.
     */
    private boolean shouldReplaceArmor(ItemStack current, int armorType) {
        if (current == null) return true;
        
        if (!(current.getItem() instanceof ItemArmor)) return true;
        
        // Check durability
        float durabilityPercent = ((float) (current.getMaxDamage() - current.getItemDamage()) / current.getMaxDamage()) * 100;
        if (durabilityPercent < durabilityThreshold.getValue()) {
            return true;
        }
        
        // Check if better armor exists
        ItemStack better = findBestArmor(armorType);
        if (better != null && getArmorScore(better) > getArmorScore(current)) {
            return true;
        }
        
        return false;
    }

    /**
     * Finds the best armor piece for the given slot.
     */
    private ItemStack findBestArmor(int armorType) {
        ItemStack best = null;
        float bestScore = -1;

        int start = hotbarOnly.getValue() ? 0 : 9;
        int end = hotbarOnly.getValue() ? 9 : 36;

        for (int i = start; i < end; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            
            if (stack == null || !(stack.getItem() instanceof ItemArmor)) continue;
            
            ItemArmor armor = (ItemArmor) stack.getItem();
            if (armor.armorType != armorType) continue;
            
            float score = getArmorScore(stack);
            if (score > bestScore) {
                bestScore = score;
                best = stack;
            }
        }

        return best;
    }

    /**
     * Calculates armor score based on mode.
     */
    private float getArmorScore(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemArmor)) return 0;
        
        ItemArmor armor = (ItemArmor) stack.getItem();
        float score = 0;

        String currentMode = mode.getMode();
        
        if (currentMode.equals("Best")) {
            // Protection value + enchantments
            score = armor.damageReduceAmount;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 1.25f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) * 0.8f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) * 0.6f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) * 0.6f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) * 0.5f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.3f;
            
        } else if (currentMode.equals("Balanced")) {
            // Balance between protection and durability
            score = armor.damageReduceAmount;
            float durabilityPercent = ((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage());
            score *= durabilityPercent; // Multiply by durability percentage
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 1.0f;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.5f;
            
        } else if (currentMode.equals("Durability")) {
            // Prioritize durability
            float durabilityPercent = ((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage());
            score = durabilityPercent * 100;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 10f;
            score += armor.damageReduceAmount * 0.5f; // Still consider protection but less
        }

        return score;
    }

    /**
     * Gets the inventory slot of the armor piece.
     */
    private int getArmorSlot(ItemStack armor, int armorType) {
        int start = hotbarOnly.getValue() ? 0 : 9;
        int end = hotbarOnly.getValue() ? 9 : 36;

        for (int i = start; i < end; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == armor) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Equips armor from inventory slot to armor slot.
     */
    private void equipArmor(int inventorySlot, int armorType) {
        if (mc.playerController == null) return;

        // Shift-click to equip
        // Slot calculation: 9-35 for main inventory, 0-8 for hotbar
        int windowSlot = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
        
        mc.playerController.windowClick(
            mc.thePlayer.inventoryContainer.windowId,
            windowSlot,
            0,
            1, // Shift-click
            mc.thePlayer
        );

        lastEquipTime = System.currentTimeMillis();
        isEquipping = true;
        
        if (logger.isDebugEnabled()) {
            logger.debug("Equipped armor from slot " + inventorySlot + " to armor slot " + armorType);
        }
    }

    /**
     * Gets random delay with Gaussian distribution.
     */
    private long getRandomDelay() {
        double mean = (minDelay.getValue() + maxDelay.getValue()) / 2.0;
        double stdDev = (maxDelay.getValue() - minDelay.getValue()) / 4.0;
        return (long) RandomUtils.gaussianRandomClamped(mean, stdDev, minDelay.getValue(), maxDelay.getValue());
    }

    @Override
    protected void onDisable() {
        isEquipping = false;
        currentSlot = -1;
    }
}
