package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ReachMod extends Module {

    public final NumberSetting reachDistance = addSetting(
            new NumberSetting("Reach", "Attack distance", 3.0f, 6.0f, 0.1f, 3.5f));

    private boolean registered = false;

    public ReachMod() { super("Reach"); }

    @Override
    protected void onEnable() {
        if (!registered) { MinecraftForge.EVENT_BUS.register(this); registered = true; }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        Entity rve = mc.getRenderViewEntity();
        if (rve == null) return;

        float reach = reachDistance.getValue();
        float pt    = event.partialTicks;
        Vec3  eye   = rve.getPositionEyes(pt);
        Vec3  look  = rve.getLook(pt);
        Vec3  end   = eye.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        MovingObjectPosition blockHit = mc.theWorld.rayTraceBlocks(eye, end, false, false, true);
        double maxReach = reach;
        if (blockHit != null) { maxReach = eye.distanceTo(blockHit.hitVec); end = blockHit.hitVec; }
        if (maxReach <= 3.0) return;

        @SuppressWarnings("unchecked")
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(rve,
            rve.getEntityBoundingBox()
               .addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach)
               .expand(1.0, 1.0, 1.0));

        Entity best = null; double bestDist = maxReach; Vec3 bestVec = null;
        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            float s = e.getCollisionBorderSize();
            AxisAlignedBB bb = e.getEntityBoundingBox().expand(s, s, s);
            MovingObjectPosition mop = bb.calculateIntercept(eye, end);
            if (bb.isVecInside(eye)) {
                if (bestDist >= 0) { best = e; bestDist = 0; bestVec = eye; }
            } else if (mop != null) {
                double d = eye.distanceTo(mop.hitVec);
                if (d < bestDist) { best = e; bestDist = d; bestVec = mop.hitVec; }
            }
        }
        if (best != null) mc.objectMouseOver = new MovingObjectPosition(best, bestVec);
    }
}
