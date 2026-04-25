package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class HoleESPModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 5.0, 1.0, 10.0, 1.0);
    private final List<BlockPos> holes = new ArrayList<>();

    public HoleESPModule() {
        super("HoleESP", "Highlights safe holes in combat.", Category.RENDER);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        holes.clear();

        int r = (int) Math.ceil(range.getValue());
        BlockPos playerPos = mc.player.getBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (isHole(pos)) {
                        holes.add(pos);
                    }
                }
            }
        }
    }

    private boolean isHole(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) return false;
        if (!mc.world.getBlockState(pos.up()).isAir()) return false;

        BlockPos[] sides = {pos.north(), pos.south(), pos.east(), pos.west(), pos.down()};
        for (BlockPos side : sides) {
            if (mc.world.getBlockState(side).getBlock() != Blocks.OBSIDIAN && 
                mc.world.getBlockState(side).getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }
}
