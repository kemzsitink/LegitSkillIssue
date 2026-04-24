package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;

public class SlimeJumpModule extends Module {
    public final NumberSetting boost = new NumberSetting("Boost", 2.0, 1.0, 5.0, 0.1);

    public SlimeJumpModule() {
        super("SlimeJump", "Jumps higher on slime blocks.", Category.MOVEMENT);
        addSetting(boost);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.SLIME_BLOCK) {
            if (mc.options.jumpKey.isPressed()) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0.42 * boost.getValue(), vel.z);
            }
        }
    }
}
