package com.example.ablabla.module.impl.player;

import com.example.ablabla.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEatMod extends Module {

    public FastEatMod() {
        super("FastEat");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;

        if (mc.thePlayer.isUsingItem()) {
            ItemStack stack = mc.thePlayer.getCurrentEquippedItem();

            if (stack != null && (stack.getItem() instanceof ItemFood || stack.getItem() instanceof ItemPotion)) {
                // Wait 1 tick before spamming to avoid AC suspicion
                if (mc.thePlayer.getItemInUseDuration() >= 1) {
                    // Send 32 position packets = simulate 32 ticks of eating in 1 tick
                    for (int i = 0; i < 32; i++) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
            }
        }
    }
}
