package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;

public class JesusMod extends Module {

    public JesusMod() {
        super("Jesus", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
        if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.motionY = 0.05;
            mc.thePlayer.onGround = true; // Tell server we are on ground
        }
    }
}
