package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class BlockESPModule extends Module {
    private final List<BlockPos> blocks = new ArrayList<>();

    public BlockESPModule() {
        super("BlockESP", "Highlights specific blocks.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        blocks.clear();

        BlockPos playerPos = mc.player.getBlockPos();
        int r = 16; // Search range

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    var block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE || block == Blocks.CHEST) {
                        blocks.add(pos);
                    }
                }
            }
        }
    }
}
