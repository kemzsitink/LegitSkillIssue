package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class WTapMod extends Module {

    private int ticks = 0;
    private boolean tapping = false;

    public WTapMod() { super("WTap"); }

    @Override
    public void onTick() {
        if (!tapping) return;
        ticks++;
        if (ticks == 2) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
        } else if (ticks == 3) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
            mc.thePlayer.setSprinting(true);
            tapping = false;
            ticks = 0;
        }
    }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        if (!(packet instanceof C02PacketUseEntity)) return false;
        if (((C02PacketUseEntity) packet).getAction() != C02PacketUseEntity.Action.ATTACK) return false;
        mc.addScheduledTask(() -> {
            if (mc.thePlayer != null && mc.thePlayer.isSprinting() && mc.gameSettings.keyBindForward.isKeyDown()) {
                tapping = true;
                ticks = 0;
            }
        });
        return false;
    }
}
