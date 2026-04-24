package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.BlockPos;

public class SafeWalkModule extends Module {
    public SafeWalkModule() {
        super("SafeWalk", "Prevents you from falling off edges.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isOnGround()) {
            BlockPos below = mc.player.getBlockPos().down();
            if (mc.world.getBlockState(below).isAir()) {
                // This is very basic. Real SafeWalk modifies the movement vector.
                // For now we just press sneak.
                mc.options.sneakKey.setPressed(true);
            } else {
                // Only release if not manually pressing
                if (!mc.options.sneakKey.isPressed()) {
                    // mc.options.sneakKey.setPressed(false);
                }
            }
        }
    }
}
