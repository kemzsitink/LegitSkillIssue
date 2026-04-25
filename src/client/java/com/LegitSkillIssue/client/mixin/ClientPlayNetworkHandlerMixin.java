package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.VelocityModule;
import com.LegitSkillIssue.client.module.exploit.BypassModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && packet.getEntityId() == mc.player.getId()) {
            VelocityModule velocity = (VelocityModule) ModuleManager.INSTANCE.getModules().stream()
                    .filter(m -> m instanceof VelocityModule)
                    .findFirst().orElse(null);

            if (velocity != null && velocity.isEnabled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        BypassModule bypass = (BypassModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof BypassModule)
                .findFirst().orElse(null);

        if (bypass != null && bypass.isEnabled()) {
            // Logic to ignore server rotations would go here
        }
    }
}
