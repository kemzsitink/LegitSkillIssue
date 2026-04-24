package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AutoToolModule extends Module {
    public AutoToolModule() {
        super("AutoTool", "Automatically switches to the best tool.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (mc.options.attackKey.isPressed() && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            BlockState state = mc.world.getBlockState(pos);
            
            int bestSlot = -1;
            double maxSpeed = 1.0;

            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                double speed = stack.getMiningSpeedMultiplier(state);
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                    bestSlot = i;
                }
            }

            if (bestSlot != -1) {
                mc.player.getInventory().selectedSlot = bestSlot;
            }
        }
    }
}
