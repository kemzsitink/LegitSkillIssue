package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;

public class EagleMod extends Module {

    public EagleMod() {
        super("Eagle", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!mc.thePlayer.onGround) return;

        // Only activate when holding a block item
        boolean holdingBlock = mc.thePlayer.getCurrentEquippedItem() != null
                && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock;
        if (!holdingBlock) return;

        boolean isEdge = true;
        for (int i = 1; i <= 3; i++) {
            BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(below).getBlock() != Blocks.air) {
                isEdge = false;
                break;
            }
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), isEdge);
    }

    @Override
    public void onDisable() {
        if (mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }
}
