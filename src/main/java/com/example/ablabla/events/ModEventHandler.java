package com.example.ablabla.events;

import com.example.ablabla.gui.ReachMenu;
import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.module.impl.AutoClickerMod;
import com.example.ablabla.module.impl.FastPlaceMod;
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
        if (key == Keyboard.KEY_RSHIFT || key == Keyboard.KEY_R) {
            if (Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().displayGuiScreen(new ReachMenu());
            }
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        // Dispatch to all modules for logic (like Reach)
        ModuleManager.INSTANCE.onMouseClick(event);

        // Detect side mouse buttons (Mouse 4 and Mouse 5) for toggling
        if (event.buttonstate) { // Only trigger on press, not release
            if (event.button == 3) { // Mouse 4
                Module ac = ModuleManager.INSTANCE.getModule(AutoClickerMod.class);
                if (ac != null) ac.toggle();
            } else if (event.button == 4) { // Mouse 5
                Module fp = ModuleManager.INSTANCE.getModule(FastPlaceMod.class);
                if (fp != null) fp.toggle();
            }
        }
    }

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // Inject our custom Netty pipeline handler when joining a server
        PacketHandler.inject();
    }

    @SubscribeEvent
    public void onServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Clean up the pipeline when disconnecting
        PacketHandler.remove();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Dispatch ticks to our Module Manager
        ModuleManager.INSTANCE.onTick();
    }
}
