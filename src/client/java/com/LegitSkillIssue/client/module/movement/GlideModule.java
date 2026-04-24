package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class GlideModule extends Module {
    public final NumberSetting fallSpeed = new NumberSetting("Fall Speed", 0.1, 0.0, 0.5, 0.01);

    public GlideModule() {
        super("Glide", "Makes you fall slower.", Category.MOVEMENT);
        addSetting(fallSpeed);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (!mc.player.isOnGround() && mc.player.getVelocity().y < 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, -fallSpeed.getValue(), vel.z);
        }
    }
}
