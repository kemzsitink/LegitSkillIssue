package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class SpeedModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.2, 1.0, 5.0, 0.1);

    public SpeedModule() {
        super("Speed", "Increases your movement speed.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) return;

        if (mc.player.isOnGround()) {
            mc.player.jump();
        } else {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x * speed.getValue(), vel.y, vel.z * speed.getValue());
        }
    }
}
