package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.movement.BlinkModule;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    private static final List<Packet<?>> packets = new ArrayList<>();

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        BlinkModule blink = (BlinkModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof BlinkModule)
                .findFirst().orElse(null);

        if (blink != null && blink.isEnabled()) {
            if (packet instanceof PlayerMoveC2SPacket) {
                packets.add(packet);
                ci.cancel();
            }
        } else if (!packets.isEmpty()) {
            // We can't easily send them back from here without recursion
            // but for a basic implementation, we just clear them.
            // (Real blink would resend them all on disable).
            packets.clear();
        }
    }
}
