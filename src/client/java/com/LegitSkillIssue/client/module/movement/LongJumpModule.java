package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class LongJumpModule extends Module {
    public final NumberSetting boost = new NumberSetting("Boost", 1.5, 1.0, 5.0, 0.1);

    public LongJumpModule() {
        super("LongJump", "Makes you jump further.", Category.MOVEMENT);
        addSetting(boost);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (!mc.player.isOnGround() && mc.player.getVelocity().y > 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x * boost.getValue(), vel.y, vel.z * boost.getValue());
        }
    }
}
