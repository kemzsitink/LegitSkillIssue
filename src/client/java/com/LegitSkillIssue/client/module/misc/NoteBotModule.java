package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import java.util.stream.StreamSupport;

public class NoteBotModule extends Module {
    private int ticks = 0;

    public NoteBotModule() {
        super("NoteBot", "Plays music on nearby note blocks.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        ticks++;
        if (ticks % 10 == 0) { // Every 0.5 seconds
            BlockPos playerPos = mc.player.getBlockPos();
            int r = 5;
            
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    for (int z = -r; z <= r; z++) {
                        BlockPos pos = playerPos.add(x, y, z);
                        if (mc.world.getBlockState(pos).getBlock() == Blocks.NOTE_BLOCK) {
                            mc.interactionManager.attackBlock(pos, net.minecraft.util.math.Direction.UP);
                            mc.player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}
