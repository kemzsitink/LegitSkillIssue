package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.movement.BlinkModule;
import com.LegitSkillIssue.client.module.player.NoFallModule;
import com.LegitSkillIssue.client.module.player.AntiHungerModule;
import com.LegitSkillIssue.client.module.world.PingSpoofModule;
import com.LegitSkillIssue.client.module.exploit.PacketLoggerModule;
import com.LegitSkillIssue.client.module.exploit.IPLeaksModule;
import com.LegitSkillIssue.client.module.exploit.LagSwitchModule;
import com.LegitSkillIssue.client.module.misc.ChatBypassModule;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
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
        ChatBypassModule chatBypass = (ChatBypassModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof ChatBypassModule)
                .findFirst().orElse(null);

        if (chatBypass != null && chatBypass.isEnabled()) {
            if (packet instanceof ChatMessageC2SPacket) {
                // Logic to modify chat message would go here.
                // Since ChatMessageC2SPacket content is final and signed in 1.21, 
                // we would need to intercept higher up in the chat system.
            }
        }

        LagSwitchModule lagSwitch = (LagSwitchModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof LagSwitchModule)
                .findFirst().orElse(null);

        if (lagSwitch != null && lagSwitch.isEnabled()) {
            ci.cancel();
            return;
        }

        IPLeaksModule ipLeaks = (IPLeaksModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof IPLeaksModule)
                .findFirst().orElse(null);

        if (ipLeaks != null && ipLeaks.isEnabled()) {
            if (packet instanceof QueryBlockNbtC2SPacket) {
                ci.cancel();
                return;
            }
        }

        PacketLoggerModule logger = (PacketLoggerModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof PacketLoggerModule)
                .findFirst().orElse(null);

        if (logger != null && logger.isEnabled()) {
            System.out.println("[PacketLogger] Sending: " + packet.getClass().getSimpleName());
        }

        BlinkModule blink = (BlinkModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof BlinkModule)
                .findFirst().orElse(null);

        if (blink != null && blink.isEnabled()) {
            if (packet instanceof PlayerMoveC2SPacket) {
                packets.add(packet);
                ci.cancel();
                return;
            }
        }

        AntiHungerModule antiHunger = (AntiHungerModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AntiHungerModule)
                .findFirst().orElse(null);

        if (antiHunger != null && antiHunger.isEnabled()) {
            if (packet instanceof ClientCommandC2SPacket commandPacket) {
                if (commandPacket.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
