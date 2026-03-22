package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class HitBoxMod extends Module {
    private float expandSize = 0.25f;

    public HitBoxMod() {
        super("HitBox");
    }

    @Override
    public void onTick() {
        if (mc.theWorld == null) return;

        // Quét tất cả người chơi trong thế giới
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer || player.isDead) continue;

            // Mở rộng HitBox (BoundingBox) của đối thủ
            // Lưu ý: Ghost HitBox chuyên nghiệp chỉ nên thay đổi kích thước va chạm, không nên đổi visual
            float size = 0.6f + expandSize;
            player.width = size;
            player.height = 1.8f + expandSize;
            
            // Re-calculate the actual AABB object
            player.setEntityBoundingBox(new net.minecraft.util.AxisAlignedBB(
                player.posX - size / 2.0F, player.posY, player.posZ - size / 2.0F,
                player.posX + size / 2.0F, player.posY + player.height, player.posZ + size / 2.0F
            ));
        }
    }

    @Override
    public void onDisable() {
        if (mc.theWorld == null) return;
        
        // Trả lại kích thước chuẩn khi tắt mod
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            player.width = 0.6f;
            player.height = 1.8f;
        }
    }
}
