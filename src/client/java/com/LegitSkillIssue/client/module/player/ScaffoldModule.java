package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ScaffoldModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 50.0);
    private long lastPlace = 0;

    public ScaffoldModule() {
        super("Scaffold", "Automatically places blocks under you.", Category.PLAYER);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastPlace < delay.getValue()) return;

        BlockPos pos = mc.player.getBlockPos().down();
        if (mc.world.getBlockState(pos).isAir()) {
            // Find block in hotbar
            for (int i = 0; i < 9; i++) {
                if (mc.player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                    int oldSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = i;
                    
                    // Basic place logic
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, 
                        new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                    
                    mc.player.getInventory().selectedSlot = oldSlot;
                    lastPlace = System.currentTimeMillis();
                    return;
                }
            }
        }
    }
}
