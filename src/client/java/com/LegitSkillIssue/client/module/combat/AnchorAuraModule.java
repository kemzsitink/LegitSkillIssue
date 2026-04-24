package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AnchorAuraModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.5, 1.0, 6.0, 0.1);

    public AnchorAuraModule() {
        super("AnchorAura", "Automatically explodes nearby Respawn Anchors.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        
        // Don't explode anchors in the Overworld unless you want to suicide
        if (mc.world.getDimension().respawnAnchorWorks()) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = (int) Math.ceil(range.getValue());
        
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.RESPAWN_ANCHOR) {
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
