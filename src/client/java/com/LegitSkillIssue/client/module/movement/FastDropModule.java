package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class FastDropModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1);

    public FastDropModule() {
        super("FastDrop", "Makes you fall faster.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (!mc.player.isOnGround() && mc.player.getVelocity().y < 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, vel.y - (speed.getValue() * 0.1), vel.z);
        }
    }
}
