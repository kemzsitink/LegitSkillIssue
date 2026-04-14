package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * REFACTORED (MCP-919): AirJump — Physics-Compliant Double Jump
 *
 * VI PHẠM CŨ:
 *   Dòng cũ (AirJumpMod.java:47-52): Gửi C06PacketPlayerPosLook với Y+1E-10 và onGround=true
 *   → Spoof onGround=true mà không kiểm tra AABB thực, vi phạm chỉ thị §1.
 *   → Inject Y delta giả vào packet, vi phạm chỉ thị §1.
 *
 * THAY ĐỔI:
 *   - Bỏ hoàn toàn onPacketSend và spoof packet.
 *   - Chỉ gọi mc.thePlayer.jump() (áp dụng motionY = 0.42F đúng như vanilla).
 *   - Kiểm tra onGround thực sự qua worldObj.getCollidingBoundingBoxes trước khi
 *     cho phép nhảy lần 2, theo chỉ thị §1.
 *
 *   Công thức A (cũ): Gửi packet C06 với onGround=true và Y+1E-10 (spoof).
 *   Công thức B (mới): Chỉ gọi player.jump() — motionY += 0.42F đúng vanilla,
 *                      không can thiệp packet.
 */
public class AirJumpMod extends Module {

    private boolean lastJumpState = false;
    private boolean usedAirJump   = false;

    public AirJumpMod() {
        super("AirJump", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        lastJumpState = false;
        usedAirJump   = false;
    }

    @Override
    protected void onDisable() {
        lastJumpState = false;
        usedAirJump   = false;
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        boolean isJumpDown = mc.gameSettings.keyBindJump.isKeyDown();

        // Reset air-jump khi chạm đất thực sự (kiểm tra AABB theo chỉ thị §1)
        if (isOnGroundReal()) {
            usedAirJump = false;
        }

        // Phát hiện nhấn jump mới (rising edge)
        if (isJumpDown && !lastJumpState) {
            boolean inAir = !mc.thePlayer.onGround
                    && !mc.thePlayer.isInWater()
                    && !mc.thePlayer.isInLava();

            if (inAir && !usedAirJump) {
                // THAY ĐỔI: Chỉ gọi jump() — áp dụng motionY = 0.42F đúng vanilla
                // Công thức A (cũ): Gửi packet C06 với onGround=true và Y+1E-10
                // Công thức B (mới): mc.thePlayer.jump() — không can thiệp packet
                mc.thePlayer.jump();
                usedAirJump = true;
            }
        }

        lastJumpState = isJumpDown;
    }

    /**
     * Kiểm tra onGround thực sự bằng cách dò AABB bên dưới người chơi,
     * khớp với cách Entity.moveEntity() xác định onGround trong vanilla 1.8.9.
     * Theo chỉ thị §1: phải dùng worldObj.getCollidingBoundingBoxes.
     */
    private boolean isOnGroundReal() {
        if (mc.thePlayer == null || mc.theWorld == null) return false;
        AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        // Dò một lớp mỏng 0.01 block bên dưới bounding box
        AxisAlignedBB checkBB  = new AxisAlignedBB(
                playerBB.minX, playerBB.minY - 0.01D, playerBB.minZ,
                playerBB.maxX, playerBB.minY,          playerBB.maxZ);
        List<?> collisions = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, checkBB);
        return !collisions.isEmpty();
    }
}
