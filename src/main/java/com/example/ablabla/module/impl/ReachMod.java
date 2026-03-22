package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;

import java.util.List;
import java.util.Random;

public class ReachMod extends Module {
    public static float reachDistance = 3.5f;
    private final Random random = new Random();

    public ReachMod() {
        super("Reach");
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        // Chỉ hoạt động khi nhấn chuột trái (nút 0)
        if (event.button != 0 || !event.buttonstate) return;

        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Tính toán Reach với một chút sai số Gaussian để "Legit" hơn
        double reach = reachDistance;
        double gaussianOffset = Math.abs(random.nextGaussian() * 0.12);
        if (gaussianOffset > 0.4) gaussianOffset = 0.4; // Giới hạn sai số
        
        reach = 3.0 + (reachDistance - 3.0) + (gaussianOffset - 0.1);
        
        if (reach < 3.0) reach = 3.0;
        if (reach > reachDistance) reach = reachDistance;

        Entity rve = mc.getRenderViewEntity();
        if (rve == null) return;

        Vec3 eye = rve.getPositionEyes(1.0f);
        Vec3 look = rve.getLook(1.0f);
        Vec3 targetVec = eye.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        // Kiểm tra xem có block nào chắn giữa không
        MovingObjectPosition block = mc.theWorld.rayTraceBlocks(eye, targetVec, false, false, true);
        if (block != null) {
            targetVec = block.hitVec;
            reach = eye.distanceTo(targetVec);
        }

        Entity target = null;
        double bestDist = reach;
        Vec3 hitVec = null;
        
        // Quét các thực thể trong tầm Reach
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(rve, 
            rve.getEntityBoundingBox().addCoord(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach).expand(1.0, 1.0, 1.0));

        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            
            float s = e.getCollisionBorderSize();
            AxisAlignedBB bb = e.getEntityBoundingBox().expand(s, s, s);
            MovingObjectPosition mop = bb.calculateIntercept(eye, targetVec);
            
            if (bb.isVecInside(eye)) {
                if (bestDist >= 0.0) { target = e; bestDist = 0.0; hitVec = eye; }
            } else if (mop != null) {
                double d = eye.distanceTo(mop.hitVec);
                if (d < bestDist || bestDist == 0.0) { target = e; bestDist = d; hitVec = mop.hitVec; }
            }
        }

        // Nếu tìm thấy mục tiêu ở xa hơn 3 blocks, ép Minecraft nhắm vào nó
        if (target != null && bestDist <= reach) {
            mc.objectMouseOver = new MovingObjectPosition(target, hitVec);
        }
    }
}
