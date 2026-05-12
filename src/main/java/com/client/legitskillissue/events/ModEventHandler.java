package com.client.legitskillissue.events;

import com.client.legitskillissue.gui.ClickGUI;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.module.impl.player.FastDropMod;
import com.client.legitskillissue.utils.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import com.client.legitskillissue.event.EventBus;
import com.client.legitskillissue.event.impl.EventRender2D;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModEventHandler {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            EventBus.INSTANCE.post(new EventRender2D(event.partialTicks));
        }
    }

    private com.client.legitskillissue.gui.TabGUI tabGUI;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;

        int key = Keyboard.getEventKey();

        // Initialize TabGUI reference lazily if needed, but it registers itself to EventBus for rendering.
        // We need it here for key handling. Actually, better to have a static instance or manager.
        // Let's just find it if possible, or use a simple hack for now since it's a small project.
        // Actually, let's just make it a static field in LegitSkillIssueMod or similar.
        
        // For now, I'll just check if it's an arrow key and handle it.
        if (key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_RETURN) {
            // Find the TabGUI instance and call onKey.
            // Since I don't have a manager, I'll just use a static instance in TabGUI.
            com.client.legitskillissue.gui.TabGUI.INSTANCE.onKey(key);
        }

        // Open GUI with Right Shift
        if (key == Keyboard.KEY_RSHIFT && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new ClickGUI());
            return;
        }

        // FastDrop key handling
        if (key == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()) {
            FastDropMod fastDrop = ModuleManager.INSTANCE.getModule(FastDropMod.class);
            if (fastDrop != null) fastDrop.onDropKey();
        }

        // Dispatch to module keybinds
        ModuleManager.INSTANCE.onKey(key);
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        ModuleManager.INSTANCE.onMouseClick(event);
    }

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        PacketHandler.inject();
        com.client.legitskillissue.utils.TpsTracker.INSTANCE.reset();
    }

    @SubscribeEvent
    public void onServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketHandler.remove();
        com.client.legitskillissue.utils.TpsTracker.INSTANCE.reset();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player != Minecraft.getMinecraft().thePlayer) return;
        
        if (event.phase == TickEvent.Phase.START) {
            EventBus.INSTANCE.post(new com.client.legitskillissue.event.impl.EventUpdate(true));
        } else if (event.phase == TickEvent.Phase.END) {
            EventBus.INSTANCE.post(new com.client.legitskillissue.event.impl.EventUpdate(false));
        }
    }

    @SubscribeEvent
    public void onRenderWorld(net.minecraftforge.client.event.RenderWorldLastEvent event) {
        EventBus.INSTANCE.post(new com.client.legitskillissue.event.impl.EventRender3D(event.partialTicks));
    }
}
