package com.example.ablabla.events;

import com.example.ablabla.gui.ReachMenu;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.utils.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

public class ModEventHandler {

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;

        int key = Keyboard.getEventKey();

        // Open GUI with R key
        if (key == Keyboard.KEY_R && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new ReachMenu());
            return;
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
    }

    @SubscribeEvent
    public void onServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketHandler.remove();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        ModuleManager.INSTANCE.onTick();
    }
}
