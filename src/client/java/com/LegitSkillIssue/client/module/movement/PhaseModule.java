package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PhaseModule extends Module {
    public PhaseModule() {
        super("Phase", "Allows you to walk through blocks.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        mc.player.noClip = true;
        
        // Basic teleport bypass for some servers: move forward slightly inside block
        if (mc.player.horizontalCollision) {
            double yaw = Math.toRadians(mc.player.getYaw());
            double x = -Math.sin(yaw) * 0.05;
            double z = Math.cos(yaw) * 0.05;
            
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX() + x, mc.player.getY(), mc.player.getZ() + z, mc.player.isOnGround(), mc.player.horizontalCollision));
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}
