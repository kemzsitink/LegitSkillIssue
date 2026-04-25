package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class CaveFinderModule extends Module {
    private final List<BlockPos> caves = new ArrayList<>();

    public CaveFinderModule() {
        super("CaveFinder", "Finds air pockets underground.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        caves.clear();

        BlockPos playerPos = mc.player.getBlockPos();
        int r = 32;

        for (int x = -r; x <= r; x += 2) { // Step 2 to save performance
            for (int y = -r; y <= r; y += 2) {
                for (int z = -r; z <= r; z += 2) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (pos.getY() < 60 && mc.world.getBlockState(pos).isAir()) {
                        caves.add(pos);
                    }
                }
            }
        }
    }
}
