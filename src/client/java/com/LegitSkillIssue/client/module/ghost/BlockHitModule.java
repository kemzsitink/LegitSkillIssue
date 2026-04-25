package com.LegitSkillIssue.client.module.ghost;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

public class BlockHitModule extends Module {
    public BlockHitModule() {
        super("BlockHit", "Blocks with shield while attacking.", Category.GHOST);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.options == null) return;

        if (mc.options.attackKey.isPressed() && mc.player.getMainHandStack().getItem() instanceof SwordItem) {
            mc.options.useKey.setPressed(true);
        }
    }
}
