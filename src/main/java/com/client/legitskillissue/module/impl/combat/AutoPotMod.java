package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import java.util.List;

public class AutoPotMod extends Module {

    public final NumberSetting health = addSetting(new NumberSetting("Health", "Throw at health", 1f, 20f, 1f, 10f));
    public final NumberSetting delay = addSetting(new NumberSetting("Delay", "Ticks between throws", 0f, 20f, 1f, 5f));
    
    private int tickDelay = 0;

    public AutoPotMod() {
        super("AutoPot", Category.COMBAT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null) return;
        
        tickDelay++;
        if (tickDelay < delay.getInt()) return;

        if (mc.thePlayer.getHealth() <= health.getValue()) {
            int potSlot = getPotionSlot();
            if (potSlot != -1) {
                int oldSlot = mc.thePlayer.inventory.currentItem;
                
                // Switch to potion
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(potSlot));
                
                // Spoof rotation to look down
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90f, mc.thePlayer.onGround));
                
                // Throw potion
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(potSlot)));
                
                // Switch back
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(oldSlot));
                
                tickDelay = 0;
            }
        }
    }

    private int getPotionSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemPotion && ItemPotion.isSplash(stack.getMetadata())) {
                ItemPotion pot = (ItemPotion) stack.getItem();
                List<PotionEffect> effects = pot.getEffects(stack);
                if (effects != null) {
                    for (PotionEffect effect : effects) {
                        if (effect.getPotionID() == Potion.heal.getId()) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }
}
