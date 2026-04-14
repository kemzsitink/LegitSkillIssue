package com.example.ablabla.module.impl.movement;

import com.example.ablabla.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class SprintMod extends Module {
    public SprintMod() {
        super("Sprint");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;
        
        // Auto-sprint: moving forward, not sneaking, not colliding horizontally
        if (mc.thePlayer.movementInput.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }
}
