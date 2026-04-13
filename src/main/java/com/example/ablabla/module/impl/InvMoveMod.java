package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvMoveMod extends Module {

    public InvMoveMod() { super("InvMove"); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.currentScreen == null) return;
        if (mc.currentScreen instanceof GuiChat) return;
        sync(mc.gameSettings.keyBindForward);
        sync(mc.gameSettings.keyBindBack);
        sync(mc.gameSettings.keyBindLeft);
        sync(mc.gameSettings.keyBindRight);
        sync(mc.gameSettings.keyBindJump);
        sync(mc.gameSettings.keyBindSprint);
    }

    private void sync(KeyBinding kb) {
        int key = kb.getKeyCode();
        if (key > 0) KeyBinding.setKeyBindState(key, Keyboard.isKeyDown(key));
    }
}
