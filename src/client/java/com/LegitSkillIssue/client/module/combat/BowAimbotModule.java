package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class BowAimbotModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 30.0, 10.0, 100.0, 1.0);

    public BowAimbotModule() {
        super("BowAimbot", "Automatically aims your bow.", Category.COMBAT);
        addSetting(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (!(mc.player.getActiveItem().getItem() instanceof BowItem)) return;

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

            float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
            float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }
    }
}
