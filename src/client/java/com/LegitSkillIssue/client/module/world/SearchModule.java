package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class SearchModule extends Module {
    private final List<BlockPos> foundBlocks = new ArrayList<>();

    public SearchModule() {
        super("Search", "Searches for specific blocks.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        foundBlocks.clear();

        BlockPos p = mc.player.getBlockPos();
        int r = 20;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = p.add(x, y, z);
                    var state = mc.world.getBlockState(pos);
                    if (state.getBlock() == Blocks.DIAMOND_ORE || state.getBlock() == Blocks.DEEPSLATE_DIAMOND_ORE) {
                        foundBlocks.add(pos);
                    }
                }
            }
        }
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (foundBlocks.isEmpty()) return;
        context.drawTextWithShadow(mc.textRenderer, "§bDiamonds found: " + foundBlocks.size(), 10, 50, -1);
    }
}
