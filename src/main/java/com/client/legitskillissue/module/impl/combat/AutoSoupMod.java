package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoSoupMod extends Module {

    public final NumberSetting health = addSetting(new NumberSetting("Health", "Eat at health", 1f, 20f, 1f, 13f));
    public final BooleanSetting drop = addSetting(new BooleanSetting("DropBowl", "Drop empty bowl", true));

    public AutoSoupMod() {
        super("AutoSoup", Category.COMBAT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null) return;

        if (mc.thePlayer.getHealth() <= health.getValue()) {
            int soupSlot = getSoupSlot();
            if (soupSlot != -1) {
                int currentSlot = mc.thePlayer.inventory.currentItem;

                // Switch to soup
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(soupSlot));
                
                // Eat soup (right click)
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(soupSlot)));
                
                // Drop bowl
                if (drop.getValue()) {
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }

                // Switch back
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(currentSlot));
            }
        }
    }

    private int getSoupSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.mushroom_stew) {
                return i;
            }
        }
        return -1;
    }
}
