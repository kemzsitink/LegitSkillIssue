package com.example.ablabla;

import com.example.ablabla.events.ModEventHandler;
import com.example.ablabla.gui.HudRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = AblablaMod.MODID, version = AblablaMod.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class AblablaMod {

    public static final String MODID   = "ablabla";
    public static final String VERSION = "1.1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModEventHandler handler = new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(handler);

        // HUD overlay (renders active modules top-right)
        MinecraftForge.EVENT_BUS.register(new HudRenderer());
    }
}
