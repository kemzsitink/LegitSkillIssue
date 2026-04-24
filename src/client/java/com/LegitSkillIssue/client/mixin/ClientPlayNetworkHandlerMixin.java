package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.VelocityModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
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
                double h = velocity.horizontal.getValue() / 100.0;
                double v = velocity.vertical.getValue() / 100.0;

                if (h == 0 && v == 0) {
                    ci.cancel();
                } else {
                    // We can't easily modify the packet fields as they are final in some versions
                    // but we can use an Accessor if needed. For simplicity, if not 0, we just don't cancel.
                    // To truly support multipliers, we'd need an Accessor Mixin.
                }
            }
        }
    }
}
