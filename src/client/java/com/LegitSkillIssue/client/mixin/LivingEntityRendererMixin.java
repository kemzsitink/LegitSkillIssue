package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.ChamsModule;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        ChamsModule chams = (ChamsModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof ChamsModule)
                .findFirst().orElse(null);

        if (chams != null && chams.isEnabled()) {
            // Placeholder for depth test manipulation
        }
    }
}
