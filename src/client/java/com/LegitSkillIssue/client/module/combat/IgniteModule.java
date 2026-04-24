package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class IgniteModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1);

    public IgniteModule() {
        super("Ignite", "Automatically ignites nearby entities.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= range.getValue())
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            for (int i = 0; i < 9; i++) {
                if (mc.player.getInventory().getStack(i).getItem() == Items.FLINT_AND_STEEL) {
                    int oldSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = i;
                    
                    BlockPos pos = target.getBlockPos();
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(target.getPos(), Direction.UP, pos, false));
                    
                    mc.player.getInventory().selectedSlot = oldSlot;
                    return;
                }
            }
        }
    }
}
