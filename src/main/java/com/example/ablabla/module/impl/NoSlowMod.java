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
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;
        if (!mc.thePlayer.isUsingItem()) return;

        boolean moving = mc.thePlayer.movementInput.moveForward != 0
                      || mc.thePlayer.movementInput.moveStrafe != 0;
        if (!moving) return;

        // Desync: tell server we released the item so it doesn't apply slow
        mc.getNetHandler().addToSendQueue(
            new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        mc.getNetHandler().addToSendQueue(
            new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
    }
}
