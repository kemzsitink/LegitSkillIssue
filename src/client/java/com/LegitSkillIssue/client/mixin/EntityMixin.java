package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.HitBoxModule;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        HitBoxModule hitBox = (HitBoxModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof HitBoxModule)
                .findFirst().orElse(null);

        if (hitBox != null && hitBox.isEnabled()) {
            double expand = hitBox.expand.getValue();
            cir.setReturnValue(cir.getReturnValue().expand(expand));
        }
    }
}
