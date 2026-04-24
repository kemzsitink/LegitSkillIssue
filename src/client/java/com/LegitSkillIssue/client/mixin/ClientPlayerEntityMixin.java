package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.movement.NoSlowModule;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean onIsUsingItem(ClientPlayerEntity player) {
        NoSlowModule noSlow = (NoSlowModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof NoSlowModule)
                .findFirst().orElse(null);

        if (noSlow != null && noSlow.isEnabled()) {
            return false;
        }
        return player.isUsingItem();
    }
}
