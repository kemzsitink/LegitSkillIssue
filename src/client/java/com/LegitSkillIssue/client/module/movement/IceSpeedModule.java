package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;

public class IceSpeedModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.5, 1.0, 5.0, 0.1);

    public IceSpeedModule() {
        super("IceSpeed", "Makes you move faster on ice.", Category.MOVEMENT);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.ICE ||
            mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.PACKED_ICE ||
            mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.BLUE_ICE) {
            
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x * speed.getValue(), vel.y, vel.z * speed.getValue());
        }
    }
}
