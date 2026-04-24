package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class HighJumpModule extends Module {
    public final NumberSetting height = new NumberSetting("Height", 1.5, 1.0, 5.0, 0.1);

    public HighJumpModule() {
        super("HighJump", "Increases your jump height.", Category.MOVEMENT);
        addSetting(height);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.options.jumpKey.isPressed() && mc.player.isOnGround()) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, 0.42 * height.getValue(), vel.z);
        }
    }
}
