package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class AimAssistModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1);
    public final NumberSetting speed = new NumberSetting("Speed", 5.0, 1.0, 20.0, 0.5);

    public AimAssistModule() {
        super("AimAssist", "Smoothly helps you aim at targets.", Category.COMBAT);
        addSetting(range);
        addSetting(speed);
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
            Vec3d targetPos = target.getEyePos();
            double diffX = targetPos.x - mc.player.getX();
            double diffY = targetPos.y - mc.player.getEyeY();
            double diffZ = targetPos.z - mc.player.getZ();
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

            float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
            float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

            float smoothYaw = MathHelper.lerpAngleDegrees((float) (speed.getValue() * 0.1f), mc.player.getYaw(), targetYaw);
            float smoothPitch = MathHelper.lerpAngleDegrees((float) (speed.getValue() * 0.1f), mc.player.getPitch(), targetPitch);

            mc.player.setYaw(smoothYaw);
            mc.player.setPitch(smoothPitch);
        }
    }
}
