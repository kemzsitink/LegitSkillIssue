package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class EagleMod extends Module {
    public EagleMod() {
        super("Eagle");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        
        // Chỉ kích hoạt khi cầm Block trên tay (Tránh việc tự dưng ngồi khi đang đánh nhau)
        boolean holdingBlock = mc.thePlayer.getCurrentEquippedItem() != null && 
                               mc.thePlayer.getCurrentEquippedItem().getItem() instanceof net.minecraft.item.ItemBlock;

        if (holdingBlock && mc.thePlayer.onGround) {
            // Kiểm tra 3 block bên dưới chân người chơi
            boolean isVoid = true;
            for (int i = 1; i <= 3; i++) {
                BlockPos posBelow = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(posBelow).getBlock() != Blocks.air) {
                    isVoid = false;
                    break;
                }
            }
            
            if (isVoid) {
                // Nếu dưới chân là khoảng không sâu ít nhất 3 block -> Tự động đè phím Shift (Sneak)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            } else {
                // Nếu bên dưới có block trong khoảng 3 block (ví dụ đang nhảy bậc thang), không tự ngồi
                // Chỉ nhả Shift nếu tay người chơi KHÔNG thực sự đè phím Shift
                if (!org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Trả lại trạng thái phím khi tắt Mod
        if (mc.gameSettings != null && !org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }
}
