package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class BackstabModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1);

    public BackstabModule() {
        super("Backstab", "Teleports behind the target and attacks.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (!mc.options.attackKey.isPressed()) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= range.getValue())
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            float yaw = target.getYaw();
            double x = target.getX() + MathHelper.sin((float) Math.toRadians(yaw)) * 1.5;
            double z = target.getZ() - MathHelper.cos((float) Math.toRadians(yaw)) * 1.5;
            
            mc.player.setPosition(x, target.getY(), z);
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
