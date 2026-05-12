package com.client.legitskillissue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.io.File;

/**
 * Automatically manages configurations based on the current server IP.
 */
public class AutoConfig {

    public static void loadForCurrentServer() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        String serverIp = (data != null) ? data.serverIP.toLowerCase().replace(":", "_") : "singleplayer";
        
        File serverConfigFile = new File(Minecraft.getMinecraft().mcDataDir, "legitskillissue/configs/" + serverIp + ".json");
        
        if (serverConfigFile.exists()) {
            Logger.getLogger(AutoConfig.class).info("Loading server-specific config for: " + serverIp);
            // In a real implementation, ConfigManager.load(serverConfigFile) would be called here.
            // For now, we'll stick to the global config but log the intent.
        }
    }
    
    public static String getCurrentServerIP() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        return (data != null) ? data.serverIP : "Singleplayer";
    }
}
