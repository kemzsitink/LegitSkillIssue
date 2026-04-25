package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Hand;

public class AutoEatModule extends Module {
    public final NumberSetting hunger = new NumberSetting("Hunger", 16.0, 1.0, 20.0, 1.0);

    public AutoEatModule() {
        super("AutoEat", "Automatically eats food.", Category.PLAYER);
        addSetting(hunger);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (mc.player.getHungerManager().getFoodLevel() <= hunger.getValue()) {
            for (int i = 0; i < 9; i++) {
                var stack = mc.player.getInventory().getStack(i);
                if (stack.get(DataComponentTypes.FOOD) != null) {
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
