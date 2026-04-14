package com.client.legitskillissue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

/**
 * REFACTORED (MCP-919): Movement helpers sử dụng đúng physics pipeline của Minecraft 1.8.9.
 *
 * THAY ĐỔI:
 *   - getBaseMoveSpeed(): Bỏ hardcode 0.2873. Tính từ attribute "movementSpeed" thực tế
 *     của entity (EntityLivingBase.getEntityAttribute), nhân hệ số potion đúng như
 *     EntityLivingBase.moveEntityWithHeading() trong vanilla 1.8.9.
 *   - applyGCD(): Giữ nguyên logic GCD nhưng thêm clamp 30°/tick theo chỉ thị §3.
 */
public class MovementUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /** Hằng số ma sát không khí (air drag) của Minecraft 1.8.9 — Entity.java */
    public static final float AIR_FRICTION   = 0.98F;
    /** Hằng số ma sát mặt đất (ground friction) của Minecraft 1.8.9 — Entity.java */
    public static final float GROUND_FRICTION = 0.91F;

    /** Giới hạn thay đổi góc tối đa mỗi tick theo chỉ thị §3 */
    public static final float MAX_ROTATION_DELTA_PER_TICK = 30.0F;

    /**
     * Kiểm tra người chơi có đang nhấn phím di chuyển không.
     */
    public static boolean isMoving() {
        return mc.thePlayer != null
                && (mc.thePlayer.movementInput.moveForward != 0
                    || mc.thePlayer.movementInput.moveStrafe != 0);
    }

    /**
     * Tính tốc độ di chuyển cơ bản từ attribute thực tế của engine.
     *
     * THAY ĐỔI tại dòng này: Từ hardcode "0.2873" sang
     *   baseSpeed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()
     * để khớp với cách EntityLivingBase.moveEntityWithHeading() tính trong vanilla 1.8.9.
     * Công thức A (cũ): double baseSpeed = 0.2873;
     * Công thức B (mới): double baseSpeed = mc.thePlayer.getEntityAttribute(
     *                        SharedMonsterAttributes.movementSpeed).getAttributeValue();
     */
    public static double getBaseMoveSpeed() {
        if (mc.thePlayer == null) return 0.2873;

        // Lấy giá trị attribute movementSpeed thực tế (đã bao gồm potion modifier từ engine)
        double baseSpeed = mc.thePlayer
                .getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .getAttributeValue();

        // Nhân thêm hệ số sprint nếu đang sprint (0.13 * baseSpeed, giống vanilla)
        if (mc.thePlayer.isSprinting()) {
            baseSpeed += baseSpeed * 0.30000001192092896D;
        }

        return baseSpeed;
    }

    /**
     * Áp dụng GCD (Greatest Common Divisor) để mô phỏng chuyển động chuột tự nhiên.
     * Thêm clamp MAX_ROTATION_DELTA_PER_TICK (30°) theo chỉ thị §3 để tránh snap đột ngột.
     *
     * THAY ĐỔI tại dòng clamp: Thêm MathHelper.clamp_float(deltaYaw, -30, 30) và
     *   MathHelper.clamp_float(deltaPitch, -30, 30) — không có trong phiên bản cũ.
     */
    public static float[] applyGCD(float yaw, float pitch, float prevYaw, float prevPitch) {
        float f   = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        float deltaYaw   = yaw   - prevYaw;
        float deltaPitch = pitch - prevPitch;

        // Snap về bội số gần nhất của GCD (mô phỏng mouse handler)
        deltaYaw   -= deltaYaw   % gcd;
        deltaPitch -= deltaPitch % gcd;

        // Clamp 30°/tick — chỉ thị §3: không thay đổi đột ngột vượt quá 30° mỗi tick
        deltaYaw   = MathHelper.clamp_float(deltaYaw,   -MAX_ROTATION_DELTA_PER_TICK, MAX_ROTATION_DELTA_PER_TICK);
        deltaPitch = MathHelper.clamp_float(deltaPitch, -MAX_ROTATION_DELTA_PER_TICK, MAX_ROTATION_DELTA_PER_TICK);

        return new float[]{ prevYaw + deltaYaw, prevPitch + deltaPitch };
    }

    /**
     * Tính vector di chuyển ngang theo đúng moveFlying() của Entity.java 1.8.9.
     * Trả về [motionX_delta, motionZ_delta] để cộng vào motion hiện tại.
     *
     * Công thức khớp với Entity.moveFlying(strafe, forward, friction):
     *   f = sqrt(strafe^2 + forward^2); if f < 1 f = 1; f = friction/f
     *   motionX += (strafe*cos(yaw) - forward*sin(yaw))
     *   motionZ += (forward*cos(yaw) + strafe*sin(yaw))
     */
    public static double[] calcMoveFlyingDelta(float strafe, float forward, float friction, float yaw) {
        float f = strafe * strafe + forward * forward;
        if (f < 1.0E-4F) return new double[]{0, 0};

        f = MathHelper.sqrt_float(f);
        if (f < 1.0F) f = 1.0F;
        f = friction / f;
        strafe  *= f;
        forward *= f;

        float sinYaw = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
        float cosYaw = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

        return new double[]{
            strafe * cosYaw - forward * sinYaw,
            forward * cosYaw + strafe * sinYaw
        };
    }
}
