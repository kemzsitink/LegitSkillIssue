package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.block.BedBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BedAuraModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.5, 1.0, 6.0, 0.1);

    public BedAuraModule() {
        super("BedAura", "Automatically explodes nearby beds in Nether/End.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        
        // Beds explode where they don't work (Nether, End)
        if (mc.world.getDimension().bedWorks()) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = (int) Math.ceil(range.getValue());
        
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() instanceof BedBlock) {
                        if (mc.player.getPos().distanceTo(Vec3d.ofCenter(pos)) <= range.getValue()) {
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, 
                                new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                            return;
                        }
                    }
                }
            }
        }
    }
}
