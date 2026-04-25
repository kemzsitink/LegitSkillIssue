package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.HitBoxModule;
import com.LegitSkillIssue.client.module.player.AntiFireModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getTargetingBoundingBox", at = @At("RETURN"), cancellable = true)
    private void onGetTargetingBoundingBox(CallbackInfoReturnable<Box> cir) {
        HitBoxModule hitBox = (HitBoxModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof HitBoxModule)
                .findFirst().orElse(null);

        if (hitBox != null && hitBox.isEnabled()) {
            double expand = hitBox.expand.getValue();
            cir.setReturnValue(cir.getReturnValue().expand(expand));
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        AntiFireModule antiFire = (AntiFireModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AntiFireModule)
                .findFirst().orElse(null);

        if (antiFire != null && antiFire.isEnabled() && (Entity)(Object)this == net.minecraft.client.MinecraftClient.getInstance().player) {
            // Basic fire check
            if (source.getName().contains("fire") || source.getName().contains("lava") || source.getName().contains("onFire")) {
                cir.setReturnValue(false);
            }
        }
    }
}
