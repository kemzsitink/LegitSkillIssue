package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;

public class CreativeModeMod extends Module {

    public CreativeModeMod() {
        super("CreativeMode");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.playerController == null) return;
        
        // 1. Ép Client chuyển sang chế độ Sáng tạo (Creative)
        mc.playerController.setGameType(WorldSettings.GameType.CREATIVE);

        // 2. Nếu đang chơi Singleplayer (Chơi Đơn), can thiệp trực tiếp vào Server Nội bộ (Integrated Server)
        if (mc.isSingleplayer()) {
            IntegratedServer server = mc.getIntegratedServer();
            if (server != null && server.getConfigurationManager() != null) {
                // Lấy bản thể Server (EntityPlayerMP) của chính bạn
                EntityPlayerMP serverPlayer = server.getConfigurationManager().getPlayerByUUID(mc.thePlayer.getUniqueID());
                if (serverPlayer != null && serverPlayer.theItemInWorldManager.getGameType() != WorldSettings.GameType.CREATIVE) {
                    // Ép Server công nhận bạn đang ở chế độ Sáng tạo
                    serverPlayer.theItemInWorldManager.setGameType(WorldSettings.GameType.CREATIVE);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null || mc.playerController == null) return;
        
        // Trả lại về chế độ Sinh tồn (Survival) trên Client
        mc.playerController.setGameType(WorldSettings.GameType.SURVIVAL);

        // Trả lại về chế độ Sinh tồn trên Server Nội bộ nếu đang chơi Singleplayer
        if (mc.isSingleplayer()) {
            IntegratedServer server = mc.getIntegratedServer();
            if (server != null && server.getConfigurationManager() != null) {
                EntityPlayerMP serverPlayer = server.getConfigurationManager().getPlayerByUUID(mc.thePlayer.getUniqueID());
                if (serverPlayer != null) {
                    serverPlayer.theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);
                }
            }
        }
    }
}
