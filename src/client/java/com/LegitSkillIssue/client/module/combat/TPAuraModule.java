package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class TPAuraModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 10.0, 5.0, 50.0, 1.0);

    public TPAuraModule() {
        super("TPAura", "Teleports to and attacks targets.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= range.getValue())
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            Vec3d oldPos = mc.player.getPos();
            
            // Teleport to target
            mc.player.setPosition(target.getX(), target.getY(), target.getZ());
            
            // Attack
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            
            // In a more advanced version, we'd teleport back here or after some ticks
        }
    }
}
