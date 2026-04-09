package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Must hook RenderWorldLastEvent — fires AFTER EntityRenderer sets objectMouseOver
 * each frame. onTick() is too slow (20/s vs 300fps), objectMouseOver gets
 * overwritten before the click is processed.
 */
public class ReachMod extends Module {

    public float reachDistance = 3.5f;
    private boolean registered = false;

    public ReachMod() {
        super("Reach");
    }

    @Override
    protected void onEnable() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(this);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        Entity rve = mc.getRenderViewEntity();
        if (rve == null) return;

        float pt = event.partialTicks;

        Vec3 eye  = rve.getPositionEyes(pt);
        Vec3 look = rve.getLook(pt);
        Vec3 end  = eye.addVector(look.xCoord * reachDistance,
                                  look.yCoord * reachDistance,
                                  look.zCoord * reachDistance);

        // Respect blocks in the way
        MovingObjectPosition blockHit = mc.theWorld.rayTraceBlocks(eye, end, false, false, true);
        double maxReach = reachDistance;
        if (blockHit != null) {
            maxReach = eye.distanceTo(blockHit.hitVec);
            end = blockHit.hitVec;
        }

        // Only bother if our reach is actually beyond vanilla (3.0)
        if (maxReach <= 3.0) return;

        @SuppressWarnings("unchecked")
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(rve,
            rve.getEntityBoundingBox()
               .addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach)
               .expand(1.0, 1.0, 1.0));

        Entity hit    = null;
        double best   = maxReach;
        Vec3   hitVec = null;

        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            float s = e.getCollisionBorderSize();
            AxisAlignedBB bb = e.getEntityBoundingBox().expand(s, s, s);
            MovingObjectPosition mop = bb.calculateIntercept(eye, end);

            if (bb.isVecInside(eye)) {
                if (best >= 0.0) { hit = e; best = 0.0; hitVec = eye; }
            } else if (mop != null) {
                double d = eye.distanceTo(mop.hitVec);
                if (d < best) { hit = e; best = d; hitVec = mop.hitVec; }
            }
        }

        if (hit != null) {
            mc.objectMouseOver = new MovingObjectPosition(hit, hitVec);
        }
    }
}
