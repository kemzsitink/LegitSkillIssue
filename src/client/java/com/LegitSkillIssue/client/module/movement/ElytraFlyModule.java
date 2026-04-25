package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class ElytraFlyModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.5, 1.0, 5.0, 0.1);

    public ElytraFlyModule() {
        super("ElytraFly", "Makes your Elytra flight faster.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.isGliding()) {
            Vec3d look = mc.player.getRotationVec(1.0f);
            mc.player.setVelocity(look.x * speed.getValue(), look.y, look.z * speed.getValue());
        }
    }
}
