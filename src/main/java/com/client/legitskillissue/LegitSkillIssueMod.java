package com.client.legitskillissue;

import com.client.legitskillissue.events.ModEventHandler;
import com.client.legitskillissue.gui.HudRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = LegitSkillIssueMod.MODID, version = LegitSkillIssueMod.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class LegitSkillIssueMod {

    public static final String MODID   = "legitskillissue";
    public static final String VERSION = "1.1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModEventHandler handler = new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(handler);

        MinecraftForge.EVENT_BUS.register(new HudRenderer());
    }
}
