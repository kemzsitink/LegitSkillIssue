package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import java.util.stream.StreamSupport;

public class AutoPotModule extends Module {
    public final NumberSetting health = new NumberSetting("Health", 10.0, 1.0, 20.0, 1.0);

    public AutoPotModule() {
        super("AutoPot", "Automatically throws health potions.", Category.COMBAT);
        addSetting(health);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (mc.player.getHealth() <= health.getValue()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.SPLASH_POTION) {
                    PotionContentsComponent potion = stack.get(DataComponentTypes.POTION_CONTENTS);
                    if (potion != null) {
                        boolean isHealing = StreamSupport.stream(potion.getEffects().spliterator(), false)
                                .anyMatch(e -> e.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
                        
                        if (!isHealing && potion.potion().isPresent()) {
                            isHealing = StreamSupport.stream(potion.potion().get().value().getEffects().spliterator(), false)
                                    .anyMatch(e -> e.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
                        }

                        if (isHealing) {
                            int oldSlot = mc.player.getInventory().selectedSlot;
                            mc.player.getInventory().selectedSlot = i;
                            float oldPitch = mc.player.getPitch();
                            mc.player.setPitch(90);
                            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                            mc.player.setPitch(oldPitch);
                            mc.player.getInventory().selectedSlot = oldSlot;
                            return;
                        }
                    }
                }
            }
        }
    }
}
