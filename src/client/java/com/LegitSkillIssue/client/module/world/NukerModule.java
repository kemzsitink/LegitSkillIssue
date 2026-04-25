package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class NukerModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.5, 1.0, 6.0, 0.1);

    public NukerModule() {
        super("Nuker", "Automatically breaks blocks around you.", Category.WORLD);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = (int) Math.ceil(range.getValue());

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!mc.world.getBlockState(pos).isAir() && mc.player.getPos().squaredDistanceTo(pos.toCenterPos()) <= range.getValue() * range.getValue()) {
                        mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        return; // Break one per tick for safety
                    }
                }
            }
        }
    }
}
