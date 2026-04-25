package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.AnimationsModule;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        AnimationsModule animations = (AnimationsModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AnimationsModule)
                .findFirst().orElse(null);

        if (animations != null && animations.isEnabled()) {
            cir.setReturnValue((int) (cir.getReturnValue() * (1.0 / animations.swingSpeed.getValue())));
        }
    }
}
