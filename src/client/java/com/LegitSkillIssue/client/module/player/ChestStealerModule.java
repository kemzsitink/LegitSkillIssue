package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class ChestStealerModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 50.0);
    private long lastTake = 0;

    public ChestStealerModule() {
        super("ChestStealer", "Automatically takes items from chests.", Category.PLAYER);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastTake < delay.getValue()) return;

        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler container = (GenericContainerScreenHandler) mc.player.currentScreenHandler;
            int size = container.getInventory().size();

            for (int i = 0; i < size; i++) {
                if (!container.getSlot(i).getStack().isEmpty()) {
                    mc.interactionManager.clickSlot(container.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                    lastTake = System.currentTimeMillis();
                    return;
                }
            }
            
            // Auto close when empty
            mc.player.closeHandledScreen();
        }
    }
}
