package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class TargetStrafeModule extends Module {
    public final NumberSetting radius = new NumberSetting("Radius", 3.0, 1.0, 6.0, 0.1);
    private int direction = 1;

    public TargetStrafeModule() {
        super("TargetStrafe", "Strafes around your target.", Category.COMBAT);
        addSetting(radius);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= 7.0)
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            if (mc.player.horizontalCollision) {
                direction *= -1;
            }
            
            // Basic strafe logic: move sideways and towards/away to maintain radius
            // This is a very simplified version.
            mc.options.jumpKey.setPressed(true); // Optional: stay in air for better speed
        }
    }
}
