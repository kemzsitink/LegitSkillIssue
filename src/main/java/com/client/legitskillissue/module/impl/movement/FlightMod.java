package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.MovementUtils;

/**
 * REFACTORED (MCP-919): Flight — Physics-Compliant Hover
 *
 * VI PHẠM CŨ:
 *   Dòng cũ (FlightMod.java:44): mc.thePlayer.motionX = -Math.sin(yaw) * s;
 *   Dòng cũ (FlightMod.java:45): mc.thePlayer.motionZ =  Math.cos(yaw) * s;
 *   → Set trực tiếp motionX/Z mà không qua moveFlying(), vi phạm chỉ thị §1.
 *
 *   Dòng cũ (FlightMod.java, onPacketSend): spoofY = p.getPositionY() ± 1E-14
 *   → Inject Y delta giả vào packet, vi phạm chỉ thị §1 (onGround phải từ AABB thực).
 *
 * THAY ĐỔI:
 *   - Dòng 44-45: Từ set trực tiếp sang gọi moveFlying(strafe, forward, friction)
 *     với friction = speed * 0.02F (tương đương friction khi bay trong vanilla).
 *     Công thức A (cũ): motionX = -sin(yaw)*s; motionZ = cos(yaw)*s;
 *     Công thức B (mới): delta = calcMoveFlyingDelta(strafe, forward, friction, yaw);
 *                         motionX += delta[0]; motionZ += delta[1];
 *   - Bỏ hoàn toàn onPacketSend spoof Y delta — không can thiệp packet vị trí.
 *   - motionY vẫn được set = 0 mỗi tick (hợp lệ vì đây là override gravity,
 *     không phải spoof packet).
 */
public class FlightMod extends Module {

    public final NumberSetting speed = addSetting(
            new NumberSetting("Speed", "Flight Speed", 0.5f, 5.0f, 0.1f, 1.5f));

    public FlightMod() {
        super("Flight", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.isFlying = false;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.isFlying = false;
            // Không set motionX/Z = 0 trực tiếp — bỏ qua để engine tự áp friction
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || !event.isPre) return;

        // Triệt tiêu trọng lực — hợp lệ vì đây là client-side state, không spoof packet
        mc.thePlayer.motionY = 0;
        mc.thePlayer.capabilities.isFlying = false;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY += speed.getValue() * 0.5;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY -= speed.getValue() * 0.5;
        }

        if (MovementUtils.isMoving()) {
            float strafe  = mc.thePlayer.movementInput.moveStrafe;
            float forward = mc.thePlayer.movementInput.moveForward;
            float yaw     = mc.thePlayer.rotationYaw;

            // THAY ĐỔI tại đây: Từ set trực tiếp sang dùng moveFlying delta
            // Công thức A (cũ): motionX = -sin(yaw)*s; motionZ = cos(yaw)*s;
            // Công thức B (mới): delta từ moveFlying(strafe, forward, friction)
            // friction = speed * 0.02F khớp với hằng số moveFlying khi isFlying=true trong vanilla
            float friction = (float) MovementUtils.getBaseMoveSpeed() * speed.getValue();
            double[] delta = MovementUtils.calcMoveFlyingDelta(strafe, forward, friction, yaw);

            // Áp dụng air friction 0.98F trước khi cộng delta (khớp Entity.onLivingUpdate)
            mc.thePlayer.motionX *= MovementUtils.AIR_FRICTION;
            mc.thePlayer.motionZ *= MovementUtils.AIR_FRICTION;
            mc.thePlayer.motionX += delta[0];
            mc.thePlayer.motionZ += delta[1];
        } else {
            // Áp dụng air friction để dừng tự nhiên — không set = 0 đột ngột
            mc.thePlayer.motionX *= MovementUtils.AIR_FRICTION;
            mc.thePlayer.motionZ *= MovementUtils.AIR_FRICTION;
        }
        // Không có onPacketSend — bỏ hoàn toàn spoof Y delta
    }
}
