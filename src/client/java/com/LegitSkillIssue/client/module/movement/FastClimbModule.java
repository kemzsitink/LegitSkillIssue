package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class FastClimbModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.5, 1.0, 5.0, 0.1);

    public FastClimbModule() {
        super("FastClimb", "Makes you climb ladders faster.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.isClimbing() && mc.player.horizontalCollision) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, speed.getValue() * 0.2, vel.z);
        }
    }
}
