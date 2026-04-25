package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class BaseFinderModule extends Module {
    private final List<BlockPos> baseBlocks = new ArrayList<>();

    public BaseFinderModule() {
        super("BaseFinder", "Finds artificial blocks (bases).", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        baseBlocks.clear();

        BlockPos playerPos = mc.player.getBlockPos();
        int r = 32;

        for (int x = -r; x <= r; x += 2) {
            for (int y = -r; y <= r; y += 2) {
                for (int z = -r; z <= r; z += 2) {
                    BlockPos pos = playerPos.add(x, y, z);
                    var block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.FURNACE || block == Blocks.CRAFTING_TABLE || block == Blocks.ENCHANTING_TABLE) {
                        baseBlocks.add(pos);
                    }
                }
            }
        }
    }
}
