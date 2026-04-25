package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class InvManagerModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 50.0);
    private long lastDrop = 0;

    public InvManagerModule() {
        super("InvManager", "Automatically cleans your inventory.", Category.PLAYER);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastDrop < delay.getValue()) return;

        // Simplified: drop dirt/gravel if found
        for (int i = 9; i < 45; i++) {
            var stack = mc.player.getInventory().getStack(i < 36 ? i : i - 36);
            if (stack.getItem() == Items.DIRT || stack.getItem() == Items.GRAVEL) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player);
                lastDrop = System.currentTimeMillis();
                return;
            }
        }
    }
}
