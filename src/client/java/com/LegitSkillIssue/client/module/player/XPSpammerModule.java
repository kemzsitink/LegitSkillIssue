package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class XPSpammerModule extends Module {
    public XPSpammerModule() {
        super("XPSpammer", "Spams experience bottles.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                int oldSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = i;
                
                float oldPitch = mc.player.getPitch();
                mc.player.setPitch(90);
                
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                
                mc.player.setPitch(oldPitch);
                mc.player.getInventory().selectedSlot = oldSlot;
                return;
            }
        }
    }
}
