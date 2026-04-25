package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.movement.TimerModule;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin {
    
    @Inject(method = "getTickDelta", at = @At("RETURN"), cancellable = true)
    private void onGetTickDelta(boolean update, CallbackInfoReturnable<Float> cir) {
        TimerModule timer = (TimerModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof TimerModule)
                .findFirst().orElse(null);

        if (timer != null && timer.isEnabled()) {
            // Timer logic for 1.21.4 would go here
        }
    }
}
