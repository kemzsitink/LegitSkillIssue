package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvMoveMod extends Module {

    public InvMoveMod() { super("InvMove", Category.MISC); }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.thePlayer == null || mc.currentScreen == null) return;
            if (mc.currentScreen instanceof GuiChat) return;
            sync(mc.gameSettings.keyBindForward);
            sync(mc.gameSettings.keyBindBack);
            sync(mc.gameSettings.keyBindLeft);
            sync(mc.gameSettings.keyBindRight);
            sync(mc.gameSettings.keyBindJump);
            sync(mc.gameSettings.keyBindSprint);
                }
    }

    private void sync(KeyBinding kb) {
        int key = kb.getKeyCode();
        if (key > 0) KeyBinding.setKeyBindState(key, Keyboard.isKeyDown(key));
    }
}
