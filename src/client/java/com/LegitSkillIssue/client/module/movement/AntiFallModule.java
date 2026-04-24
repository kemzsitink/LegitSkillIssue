package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class AntiFallModule extends Module {
    public final NumberSetting distance = new NumberSetting("Distance", 3.0, 1.0, 10.0, 0.5);

    public AntiFallModule() {
        super("AntiFall", "Prevents fall damage.", Category.MOVEMENT);
        addSetting(distance);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.fallDistance >= distance.getValue()) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, 0.1, vel.z);
            mc.player.fallDistance = 0;
        }
    }
}
