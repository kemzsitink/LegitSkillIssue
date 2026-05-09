package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;
import java.util.Random;

/**
 * REFACTORED: Perfect Reach (Unified Event & Integrated Occlusion)
 * 
 * FIX: Moved to custom EventBus and resolved conflict with HitBoxMod.
 */
public class ReachMod extends Module {

    public final NumberSetting reach = addSetting(new NumberSetting("Distance", "Max reach distance", 3.0f, 5.0f, 0.1f, 3.5f));
    public final BooleanSetting misplace = addSetting(new BooleanSetting("Misplace", "Bypass reach checks", true));
    public final NumberSetting fov = addSetting(new NumberSetting("FOV", "FOV check for reach", 10f, 180f, 5f, 90f));

    private final Random random = new Random();

    public ReachMod() {
        super("Reach", Category.COMBAT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre || mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        double currentReach = reach.getValue();
        if (misplace.getValue()) {
            currentReach += (random.nextDouble() * 0.05);
        }

        Entity pointedEntity = null;
        Vec3 eyePos = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 lookVec = mc.thePlayer.getLook(1.0f);
        Vec3 reachVec = eyePos.addVector(lookVec.xCoord * currentReach, lookVec.yCoord * currentReach, lookVec.zCoord * currentReach);

        MovingObjectPosition blockMop = mc.theWorld.rayTraceBlocks(eyePos, reachVec, false, false, true);
        double d1 = currentReach;

        if (blockMop != null) {
            d1 = blockMop.hitVec.distanceTo(eyePos);
        }

        Vec3 lookDelta = new Vec3(lookVec.xCoord * currentReach, lookVec.yCoord * currentReach, lookVec.zCoord * currentReach);
        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().addCoord(lookDelta.xCoord, lookDelta.yCoord, lookDelta.zCoord).expand(1.0D, 1.0D, 1.0D)
        );

        double d2 = d1;

        for (Entity entity : entities) {
            if (entity.canBeCollidedWith()) {
                float collisionSize = entity.getCollisionBorderSize();
                AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(collisionSize, collisionSize, collisionSize);
                MovingObjectPosition intercept = aabb.calculateIntercept(eyePos, reachVec);

                if (aabb.isVecInside(eyePos)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
                } else if (intercept != null) {
                    double d3 = eyePos.distanceTo(intercept.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (RotationUtils.isInFov(entity, fov.getValue())) {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }

        if (pointedEntity != null && (d2 < d1 || blockMop == null)) {
            mc.objectMouseOver = new MovingObjectPosition(pointedEntity, reachVec);
        }
    }
}
