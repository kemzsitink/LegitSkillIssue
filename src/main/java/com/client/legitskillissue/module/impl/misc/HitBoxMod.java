package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * REFACTORED: HitBoxMod
 * 
 * FIX: Resolved conflict with ReachMod by checking existing objectMouseOver.
 */
public class HitBoxMod extends Module {

    public final NumberSetting expand = addSetting(new NumberSetting("HitBox", "Expand size", 0f, 0.5f, 0.05f, 0.1f));

    public HitBoxMod() { super("HitBox", Category.MISC); }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.theWorld == null || mc.thePlayer == null) return;
    
            // If ReachMod or other module already found a target, don't overwrite it
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                return;
            }
    
            Vec3 eye = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 look = mc.thePlayer.getLook(1.0F);
            double reach = mc.playerController.getBlockReachDistance();
            Vec3 end = eye.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);
    
            EntityPlayer best = null;
            double bestDist = reach;
            Vec3 bestVec = null;
    
            for (EntityPlayer p : mc.theWorld.playerEntities) {
                if (p == mc.thePlayer || p.isDead) continue;
                
                AxisAlignedBB expanded = p.getEntityBoundingBox().expand(expand.getValue(), expand.getValue(), expand.getValue());
                MovingObjectPosition mop = expanded.calculateIntercept(eye, end);
                
                if (mop != null) {
                    double d = eye.distanceTo(mop.hitVec);
                    if (d < bestDist) {
                        best = p;
                        bestDist = d;
                        bestVec = mop.hitVec;
                    }
                }
            }
    
            if (best != null) {
                mc.objectMouseOver = new MovingObjectPosition(best, bestVec);
            }
                }
    }
}
