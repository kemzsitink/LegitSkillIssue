package com.LegitSkillIssue.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.gui.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

public class LegitSkillIssueClient implements ClientModInitializer {
    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        // Register Module Manager
        ModuleManager.INSTANCE.init();

        // Register GUI Key (Right Shift)
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.legitskillissue.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.legitskillissue.main"
        ));

        // Listen for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiKey.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }
        });
    }
}
