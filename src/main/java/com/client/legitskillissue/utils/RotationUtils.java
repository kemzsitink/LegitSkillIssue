package com.client.legitskillissue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

/**
 * BEST PRACTICE: Rotation Engine for Legit Behavior.
 * 
 * Key Features:
 * - GCD Simulation: Matches sensitivity to prevent anticheat flags.
 * - Smooth Interpolation: Eases movements for a natural look.
 * - Smart Clamping: Keeps rotations within legal human limits.
 */
public class RotationUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Calculates the rotation required to face a specific vector.
     */
    public static float[] getRotations(Vec3 origin, Vec3 target) {
        double diffX = target.xCoord - origin.xCoord;
        double diffY = target.yCoord - origin.yCoord;
        double diffZ = target.zCoord - origin.zCoord;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    /**
     * Smoothly rotates from current to target using sensitivity-aware steps.
     */
    public static float[] getSmoothRotations(float[] current, float[] target, float speed) {
        float yawDiff = MathHelper.wrapAngleTo180_float(target[0] - current[0]);
        float pitchDiff = MathHelper.wrapAngleTo180_float(target[1] - current[1]);

        // Human-like acceleration/deceleration simulation
        float stepYaw = yawDiff * Math.min(1.0f, speed / 100.0f);
        float stepPitch = pitchDiff * Math.min(1.0f, speed / 100.0f);

        return applyGCD(current[0] + stepYaw, current[1] + stepPitch, current[0], current[1]);
    }

    private static float yawRemainder = 0;
    private static float pitchRemainder = 0;

    /**
     * Applies Mouse Sensitivity GCD (Greatest Common Divisor) logic.
     * Prevents "Impossible Rotation" flags by aligning deltas with pixel steps.
     * Fix: Accumulates small deltas to prevent Slow Rotation Bug.
     */
    public static float[] applyGCD(float yaw, float pitch, float prevYaw, float prevPitch) {
        float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float multiplier = sensitivity * sensitivity * sensitivity * 1.2F;

        float deltaYaw = (yaw - prevYaw) + yawRemainder;
        float deltaPitch = (pitch - prevPitch) + pitchRemainder;

        float fixedDeltaYaw = deltaYaw - (deltaYaw % multiplier);
        float fixedDeltaPitch = deltaPitch - (deltaPitch % multiplier);

        yawRemainder = deltaYaw % multiplier;
        pitchRemainder = deltaPitch % multiplier;

        return new float[]{prevYaw + fixedDeltaYaw, prevPitch + fixedDeltaPitch};
    }

    /**
     * Checks if a target is within a specific FOV relative to the player's view.
     */
    public static boolean isInFov(Entity target, float fov) {
        float[] rots = getRotations(mc.thePlayer.getPositionEyes(1.0f), target.getPositionEyes(1.0f));
        float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rots[0]));
        float pitchDiff = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - rots[1]));
        return yawDiff <= fov / 2.0f && pitchDiff <= fov / 2.0f;
    }
}
