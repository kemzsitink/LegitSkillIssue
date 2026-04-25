package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.ReachModule;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void onGetEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reach = (ReachModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof ReachModule)
                .findFirst().orElse(null);

        if (reach != null && reach.isEnabled()) {
            cir.setReturnValue(reach.range.getValue());
        }
    }

    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void onGetBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        ReachModule reach = (ReachModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof ReachModule)
                .findFirst().orElse(null);

        if (reach != null && reach.isEnabled()) {
            cir.setReturnValue(reach.range.getValue());
        }
    }
}
