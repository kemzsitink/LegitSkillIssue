package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.Vec3d;

public class AntiVoidModule extends Module {
    public AntiVoidModule() {
        super("AntiVoid", "Prevents falling into the void.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.getY() < -5.0 && mc.player.getVelocity().y < 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, 0.5, vel.z);
            // In a real client, we would teleport to last ground pos
        }
    }
}
