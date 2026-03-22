package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowMod extends Module {
    public NoSlowMod() {
        super("NoSlow");
    }

    @Override
    public void onTick() {
        // Only run when moving and using an item
        if (mc.thePlayer.isUsingItem() && (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0)) {
            // Restore speed client-side mathematically
            mc.thePlayer.movementInput.moveForward *= 5.0f; 
            mc.thePlayer.movementInput.moveStrafe *= 5.0f;
            
            // Server-side bypass: Send release before movement, then placement after movement.
            // This exploits server's item-use desync mechanism.
            // Avoid spamming the console 20 times a second, log only once in a while or use a basic debug print.
            // System.out.println("[Ablabla-Logger] [NoSlow] Sending item desync packets (C07 -> C08)");
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }
}
