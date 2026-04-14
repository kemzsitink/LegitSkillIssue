package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

public class FastEatMod extends Module {

    // field_71072_f = itemInUseCount (private in EntityPlayer, MCP 1.8.9)
    private static final Field ITEM_IN_USE_COUNT = ReflectionUtil.findField(
            EntityPlayer.class, "itemInUseCount", "field_71072_f");

    public FastEatMod() {
        super("FastEat", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || ITEM_IN_USE_COUNT == null) return;
        if (!mc.thePlayer.isUsingItem()) return;

        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null) return;
        if (!(stack.getItem() instanceof ItemFood) && !(stack.getItem() instanceof ItemPotion)) return;

        if (mc.thePlayer.getItemInUseDuration() >= 1) {
            try {
                int current = ITEM_IN_USE_COUNT.getInt(mc.thePlayer);
                ITEM_IN_USE_COUNT.setInt(mc.thePlayer, Math.max(0, current - 4));
            } catch (Exception ignored) {}
        }
    }
}
