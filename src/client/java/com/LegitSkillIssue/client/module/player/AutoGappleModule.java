package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoGappleModule extends Module {
    public final NumberSetting health = new NumberSetting("Health", 14.0, 1.0, 20.0, 1.0);

    public AutoGappleModule() {
        super("AutoGapple", "Automatically eats Golden Apples.", Category.PLAYER);
        addSetting(health);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (mc.player.getHealth() <= health.getValue()) {
            for (int i = 0; i < 9; i++) {
                var stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                    int oldSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = i;
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    mc.player.getInventory().selectedSlot = oldSlot;
                    return;
                }
            }
        }
    }
}
