package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.Vec3d;

public class JesusModule extends Module {
    public JesusModule() {
        super("Jesus", "Allows you to walk on water.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.isTouchingWater() || mc.player.isInLava()) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, 0.11, vel.z);
        }
    }
}
