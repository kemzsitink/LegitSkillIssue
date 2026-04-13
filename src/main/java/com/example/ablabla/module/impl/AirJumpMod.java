package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;

public class AirJumpMod extends Module {

    private boolean wasJumping = false;

    public AirJumpMod() {
        super("AirJump");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;

        // Kiểm tra xem phím Nhảy (thường là Space) có đang được giữ không
        boolean isJumping = mc.gameSettings.keyBindJump.isKeyDown();

        // Nếu người chơi vừa mới bấm nhảy trong Tick này (chưa giữ từ trước)
        if (isJumping && !wasJumping) {
            // Nếu đang lơ lửng trên không trung
            if (!mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying) {
                // Ép nhân vật thực hiện động tác nhảy một lần nữa
                mc.thePlayer.jump();
            }
        }

        // Lưu lại trạng thái của phím nhảy cho Tick tiếp theo
        wasJumping = isJumping;
    }
    
    @Override
    public void onDisable() {
        wasJumping = false;
    }
}
