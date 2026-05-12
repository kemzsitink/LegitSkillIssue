package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class AutoToolMod extends Module {

    public AutoToolMod() {
        super("AutoTool", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null || mc.objectMouseOver == null) return;
        
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            
            float bestSpeed = 1.0f;
            int bestSlot = -1;
            
            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                    float speed = mc.thePlayer.inventory.getStackInSlot(i).getStrVsBlock(block);
                    if (speed > bestSpeed) {
                        bestSpeed = speed;
                        bestSlot = i;
                    }
                }
            }
            
            if (bestSlot != -1 && mc.thePlayer.inventory.currentItem != bestSlot) {
                mc.thePlayer.inventory.currentItem = bestSlot;
            }
        }
    }
}
