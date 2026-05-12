package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class SprintMod extends Module {
    public SprintMod() {
        super("Sprint", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.thePlayer == null) return;
            
            // Auto-sprint: moving forward, not sneaking, not colliding horizontally
            if (mc.thePlayer.movementInput.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            }
                }
    }

    @Override
    protected void onDisable() {
        if (mc.gameSettings != null) {
            int keyCode = mc.gameSettings.keyBindSprint.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, keyCode != 0 && org.lwjgl.input.Keyboard.isKeyDown(keyCode));
        }
    }
}
