package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * REFACTORED: NoFall — Physics-Compliant Fall Damage Prevention
 * 
 * FIX: Resets fallDistance when about to land to prevent damage calculation.
 */
public class NoFallMod extends Module {

    public NoFallMod() {
        super("NoFall", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Only trigger if we have fallen enough to take damage
        if (mc.thePlayer.fallDistance < 2.5f) return;
        if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) return;

        if (isAboutToLand()) {
            // Reset fall distance just before landing to prevent damage
            mc.thePlayer.fallDistance = 0;
            
            // Safer motion clamp: slow down the fall instead of a hard -0.1
            // This prevents extreme impact but looks more natural than a sudden stop
            if (mc.thePlayer.motionY < -0.2) {
                mc.thePlayer.motionY = -0.2;
            }
        }
    }

    private boolean isAboutToLand() {
        AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        double nextY = mc.thePlayer.motionY;
        AxisAlignedBB checkBB = new AxisAlignedBB(
                playerBB.minX, playerBB.minY + nextY - 0.2D, playerBB.minZ,
                playerBB.maxX, playerBB.minY,                 playerBB.maxZ);
        List<?> collisions = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, checkBB);
        return !collisions.isEmpty();
    }
}
