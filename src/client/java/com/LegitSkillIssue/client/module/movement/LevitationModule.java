package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class LevitationModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 0.1, 0.0, 1.0, 0.01);

    public LevitationModule() {
        super("Levitation", "Makes you float upwards.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        Vec3d vel = mc.player.getVelocity();
        mc.player.setVelocity(vel.x, speed.getValue(), vel.z);
    }
}
