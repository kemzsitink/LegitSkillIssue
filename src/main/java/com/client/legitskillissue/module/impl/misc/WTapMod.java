package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class WTapMod extends Module {

    private int ticks = 0;
    private boolean tapping = false;

    public WTapMod() { super("WTap", Category.MISC); }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
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
    }

    @Override
    protected void onDisable() {
        if (mc.gameSettings != null) {
            int keyCode = mc.gameSettings.keyBindForward.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, keyCode != 0 && org.lwjgl.input.Keyboard.isKeyDown(keyCode));
        }
        tapping = false;
        ticks = 0;
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
