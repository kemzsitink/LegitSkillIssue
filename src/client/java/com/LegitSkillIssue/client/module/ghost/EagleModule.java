package com.LegitSkillIssue.client.module.ghost;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.BlockPos;

public class EagleModule extends Module {
    public EagleModule() {
        super("Eagle", "Automatically sneaks at edges.", Category.GHOST);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isOnGround()) {
            BlockPos pos = mc.player.getBlockPos().down();
            if (mc.world.getBlockState(pos).isAir()) {
                mc.options.sneakKey.setPressed(true);
            } else {
                if (!mc.options.sneakKey.isPressed()) {
                    // mc.options.sneakKey.setPressed(false);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(false);
        }
    }
}
