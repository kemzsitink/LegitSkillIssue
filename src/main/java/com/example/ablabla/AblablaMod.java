package com.example.ablabla;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = AblablaMod.MODID, version = AblablaMod.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class AblablaMod {
    public static final String MODID = "ablabla";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new com.example.ablabla.events.ModEventHandler()); // Handle Mouse Click
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(new com.example.ablabla.events.ModEventHandler()); // Handle KeyInputEvent for 1.8.9
    }
}
