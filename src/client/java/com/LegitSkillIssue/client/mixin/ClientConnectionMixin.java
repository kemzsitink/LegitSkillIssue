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
    private static final List<Packet<?>> deferredPackets = new ArrayList<>();

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        NoFallModule noFall = (NoFallModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof NoFallModule)
                .findFirst().orElse(null);

        if (noFall != null && noFall.isEnabled()) {
            if (packet instanceof PlayerMoveC2SPacket movePacket) {
                ((PlayerMoveC2SPacketAccessor) movePacket).setOnGround(true);
            }
        }

        LagSwitchModule lagSwitch = (LagSwitchModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof LagSwitchModule)
                .findFirst().orElse(null);

        if (lagSwitch != null && lagSwitch.isEnabled()) {
            deferredPackets.add(packet);
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
        
        // ... Blink & other logic ...
    }
}
