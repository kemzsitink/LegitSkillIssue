package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * REFACTORED (MCP-919): ReachMod — Raytrace from Eye Position Compliant
 *
 * Kiểm tra: Khoảng cách reach đã được tính từ getPositionEyes() — đúng theo chỉ thị §3.
 *
 * THAY ĐỔI bổ sung:
 *   - Thêm kiểm tra: Nếu entity tìm được qua extended reach không khớp với
 *     mc.objectMouseOver hiện tại (entity khác đang bị block bởi block), hủy bỏ
 *     việc set mc.objectMouseOver để tránh tấn công xuyên tường.
 *   - Reach chỉ được mở rộng khi maxReach > 3.0 (survival default) — giữ nguyên.
 *
 *   Công thức A (cũ): Set mc.objectMouseOver trực tiếp không kiểm tra block occlusion.
 *   Công thức B (mới): Kiểm tra block rayTrace trước; nếu block gần hơn entity thì
 *                      không override mc.objectMouseOver (entity bị che khuất bởi block).
 */
public class ReachMod extends Module {

    public final NumberSetting reachDistance = addSetting(
            new NumberSetting("Reach", "Attack distance", 3.0f, 6.0f, 0.1f, 3.5f));

    private boolean registered = false;

    public ReachMod() { super("Reach", Category.COMBAT); }

    @Override
    protected void onEnable() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(this);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled() || mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        Entity rve = mc.getRenderViewEntity();
        if (rve == null) return;

        float reach = reachDistance.getValue();
        float pt    = event.partialTicks;

        // Chỉ thị §3: Tính reach từ mắt (getPositionEyes) — đã đúng
        Vec3 eye  = rve.getPositionEyes(pt);
        Vec3 look = rve.getLook(pt);
        Vec3 end  = eye.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        // Raytrace block trước để xác định occlusion
        MovingObjectPosition blockHit = mc.theWorld.rayTraceBlocks(eye, end, false, false, true);
        double maxReach = reach;
        if (blockHit != null) {
            maxReach = eye.distanceTo(blockHit.hitVec);
            end      = blockHit.hitVec;
        }

        // Chỉ mở rộng reach khi vượt quá survival default (3.0)
        if (maxReach <= 3.0) return;

        @SuppressWarnings("unchecked")
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(rve,
                rve.getEntityBoundingBox()
                   .addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach)
                   .expand(1.0, 1.0, 1.0));

        Entity best     = null;
        double bestDist = maxReach;
        Vec3   bestVec  = null;

        for (Entity e : list) {
            if (!e.canBeCollidedWith()) continue;
            float         s  = e.getCollisionBorderSize();
            AxisAlignedBB bb = e.getEntityBoundingBox().expand(s, s, s);
            MovingObjectPosition mop = bb.calculateIntercept(eye, end);

            if (bb.isVecInside(eye)) {
                if (bestDist >= 0) { best = e; bestDist = 0; bestVec = eye; }
            } else if (mop != null) {
                double d = eye.distanceTo(mop.hitVec);
                if (d < bestDist) { best = e; bestDist = d; bestVec = mop.hitVec; }
            }
        }

        // THAY ĐỔI: Chỉ set objectMouseOver nếu entity không bị che khuất bởi block
        // Công thức A (cũ): Set mc.objectMouseOver trực tiếp không kiểm tra occlusion
        // Công thức B (mới): Kiểm tra bestDist < maxReach (entity gần hơn block hit)
        if (best != null && bestDist < maxReach) {
            mc.objectMouseOver = new MovingObjectPosition(best, bestVec);
        }
    }
}
