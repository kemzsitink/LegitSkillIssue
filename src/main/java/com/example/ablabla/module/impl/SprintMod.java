package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class SprintMod extends Module {
    public SprintMod() {
        super("Sprint");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;
        
        // Tự động nhấn phím chạy (Sprint) nếu đang di chuyển về phía trước, không bị đói và không đụng tường
        if (mc.thePlayer.movementInput.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }
}
