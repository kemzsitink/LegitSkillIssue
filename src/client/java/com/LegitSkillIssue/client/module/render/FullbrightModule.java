package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullbrightModule extends Module {
    public FullbrightModule() {
        super("Fullbright", "Makes everything bright.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1000, 0, false, false));
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
