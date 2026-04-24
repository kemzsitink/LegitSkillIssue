package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class NoteBotModule extends Module {
    public NoteBotModule() {
        super("NoteBot", "Automatically plays nearby note blocks.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = 4;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.NOTE_BLOCK) {
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, 
                            new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                        return;
                    }
                }
            }
        }
    }
}
