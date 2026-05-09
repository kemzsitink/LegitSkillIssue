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

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;

        int key = Keyboard.getEventKey();

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
        ModuleManager.INSTANCE.onKeyPress(key);
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
            ModuleManager.INSTANCE.onTick();
        } else if (event.phase == TickEvent.Phase.END) {
            EventBus.INSTANCE.post(new com.client.legitskillissue.event.impl.EventUpdate(false));
        }
    }

    @SubscribeEvent
    public void onRenderWorld(net.minecraftforge.client.event.RenderWorldLastEvent event) {
        EventBus.INSTANCE.post(new com.client.legitskillissue.event.impl.EventRender3D(event.partialTicks));
    }
}
