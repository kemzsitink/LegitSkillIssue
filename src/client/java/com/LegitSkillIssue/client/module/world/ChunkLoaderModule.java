package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ChunkLoaderModule extends Module {
    public ChunkLoaderModule() {
        super("ChunkLoader", "Attempts to keep chunks loaded.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        // Send dummy move packets in a 3x3 chunk area
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX() + (x * 16), mc.player.getY(), mc.player.getZ() + (z * 16), 
                    mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround(), mc.player.horizontalCollision));
            }
        }
    }
}
