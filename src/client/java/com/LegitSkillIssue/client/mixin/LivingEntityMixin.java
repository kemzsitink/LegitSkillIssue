package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.AnimationsModule;
import com.LegitSkillIssue.client.module.player.AntiFireModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
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

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        AntiFireModule antiFire = (AntiFireModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AntiFireModule)
                .findFirst().orElse(null);

        if (antiFire != null && antiFire.isEnabled() && (Object)this == net.minecraft.client.MinecraftClient.getInstance().player) {
            String msgId = source.getType().msgId();
            if (msgId.contains("fire") || msgId.contains("lava") || msgId.contains("onFire")) {
                cir.setReturnValue(false);
            }
        }
    }
}
