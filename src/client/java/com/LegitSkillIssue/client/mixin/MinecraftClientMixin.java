package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.player.FastPlaceModule;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow private int itemUseCooldown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        FastPlaceModule fastPlace = (FastPlaceModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof FastPlaceModule)
                .findFirst().orElse(null);

        if (fastPlace != null && fastPlace.isEnabled()) {
            itemUseCooldown = 0;
        }
    }
}
