package com.client.legitskillissue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

/**
 * REFACTORED (MCP-919): Movement helpers using correct 1.8.9 physics.
 * 
 * FIXES:
 * - AIR_FRICTION corrected to 0.91 (horizontal).
 * - getBaseMoveSpeed() corrected to avoid double-counting modifiers.
 */
public class MovementUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /** Ground/Air drag (friction) for horizontal movement in 1.8.9 */
    public static final float HORIZONTAL_FRICTION = 0.91F;
    /** Vertical drag for falling in 1.8.9 */
    public static final float VERTICAL_DRAG = 0.98F;

    public static final float MAX_ROTATION_DELTA_PER_TICK = 30.0F;

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0);
    }

    public static double getBaseMoveSpeed() {
        if (mc.thePlayer == null) return 0.2873;

        // getAttributeValue() already includes Sprint and Potion modifiers in 1.8.9
        double baseSpeed = mc.thePlayer
                .getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .getAttributeValue();

        return baseSpeed;
    }

    public static double getSpeed() {
        if (mc.thePlayer == null) return 0;
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void setSpeed(double speed) {
        if (mc.thePlayer == null || !isMoving()) return;

        float yaw = mc.thePlayer.rotationYaw;
        float forward = mc.thePlayer.movementInput.moveForward;
        float strafe = mc.thePlayer.movementInput.moveStrafe;

        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (strafe < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }
            strafe = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        mc.thePlayer.motionX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
        mc.thePlayer.motionZ = (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
    }

    public static float[] applyGCD(float yaw, float pitch, float prevYaw, float prevPitch) {
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        float deltaYaw = yaw - prevYaw;
        float deltaPitch = pitch - prevPitch;

        deltaYaw -= deltaYaw % gcd;
        deltaPitch -= deltaPitch % gcd;

        deltaYaw = MathHelper.clamp_float(deltaYaw, -MAX_ROTATION_DELTA_PER_TICK, MAX_ROTATION_DELTA_PER_TICK);
        deltaPitch = MathHelper.clamp_float(deltaPitch, -MAX_ROTATION_DELTA_PER_TICK, MAX_ROTATION_DELTA_PER_TICK);

        return new float[]{prevYaw + deltaYaw, prevPitch + deltaPitch};
    }

    public static double[] calcMoveFlyingDelta(float strafe, float forward, float friction, float yaw) {
        float f = strafe * strafe + forward * forward;
        if (f < 1.0E-4F) return new double[]{0, 0};

        f = MathHelper.sqrt_float(f);
        if (f < 1.0F) f = 1.0F;
        f = friction / f;
        strafe *= f;
        forward *= f;

        float sinYaw = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
        float cosYaw = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

        return new double[]{
            strafe * cosYaw - forward * sinYaw,
            forward * cosYaw + strafe * sinYaw
        };
    }
}
