package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class StashFinderModule extends Module {
    private final List<BlockPos> stashes = new ArrayList<>();

    public StashFinderModule() {
        super("StashFinder", "Finds large groups of containers.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        stashes.clear();

        BlockPos playerPos = mc.player.getBlockPos();
        int r = 64;

        for (int x = -r; x <= r; x += 4) {
            for (int y = -r; y <= r; y += 4) {
                for (int z = -r; z <= r; z += 4) {
                    BlockPos pos = playerPos.add(x, y, z);
                    var block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.CHEST || block instanceof ShulkerBoxBlock) {
                        stashes.add(pos);
                    }
                }
            }
        }
    }
}
