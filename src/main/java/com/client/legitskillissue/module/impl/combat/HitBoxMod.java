package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * REFACTORED (Legitimacy Scan — Physics/Interaction):
 *
 * VI PHẠM CŨ (Physics — Bounding Box):
 *   Dòng cũ: p.width = w; p.height = h; p.setEntityBoundingBox(new AxisAlignedBB(...))
 *   → Ghi đè width/height của entity khác — làm sai collision và raytrace phía client.
 *   → Khi attackEntity() được gọi, server kiểm tra khoảng cách thực tế từ vị trí server.
 *     Client có thể tấn công entity mà server cho là ngoài tầm → flag "Invalid attack".
 *   → Thay đổi width/height là can thiệp trực tiếp vào biến vật lý không qua engine.
 *
 * SỬA:
 *   - Không thay đổi width/height của entity.
 *   - Chỉ mở rộng AABB tạm thời khi tính raytrace (expand trên bản sao, không ghi vào entity).
 *   - Dùng getEntityBoundingBox().expand() trong raytrace check thay vì set trực tiếp.
 *   - Thực tế: HitBoxMod chỉ có ý nghĩa khi kết hợp với raytrace — module này
 *     không cần set AABB vào entity, chỉ cần override objectMouseOver.
 *
 *   Công thức A (cũ): p.width = orig + expand; p.setEntityBoundingBox(expanded)
 *   Công thức B (mới): Không thay đổi entity — expand chỉ trong raytrace check
 *                      qua mc.objectMouseOver override (tương tự ReachMod)
 */
public class HitBoxMod extends Module {

    public final NumberSetting expand = addSetting(new NumberSetting("HitBox", "Expand size", 0f, 0.5f, 0.05f, 0.1f));

    // Lưu AABB gốc để restore khi disable — không còn cần thiết nhưng giữ để an toàn
    private final Map<EntityPlayer, float[]> originals = new IdentityHashMap<>();

    public HitBoxMod() { super("HitBox", Category.COMBAT); }

    @Override
    public void onTick() {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        // THAY ĐỔI: Không set width/height/AABB vào entity
        // Công thức A (cũ): p.width = orig + expand; p.setEntityBoundingBox(expanded)
        // Công thức B (mới): Chỉ override objectMouseOver nếu crosshair đang gần entity
        //   bằng cách expand AABB trên bản sao tạm thời trong raytrace check
        if (mc.objectMouseOver == null
                || mc.objectMouseOver.typeOfHit != net.minecraft.util.MovingObjectPosition.MovingObjectType.ENTITY) {

            // Thử tìm entity trong expanded hitbox
            net.minecraft.util.Vec3 eye  = mc.thePlayer.getPositionEyes(1.0F);
            net.minecraft.util.Vec3 look = mc.thePlayer.getLook(1.0F);
            double reach = mc.playerController.getBlockReachDistance();
            net.minecraft.util.Vec3 end  = eye.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

            EntityPlayer best     = null;
            double       bestDist = reach;
            net.minecraft.util.Vec3 bestVec = null;

            for (EntityPlayer p : mc.theWorld.playerEntities.toArray(new EntityPlayer[0])) {
                if (p == mc.thePlayer || p.isDead) continue;
                // Expand AABB trên bản sao — không ghi vào entity
                AxisAlignedBB expanded = p.getEntityBoundingBox().expand(expand.getValue(), expand.getValue(), expand.getValue());
                net.minecraft.util.MovingObjectPosition mop = expanded.calculateIntercept(eye, end);
                if (mop != null) {
                    double d = eye.distanceTo(mop.hitVec);
                    if (d < bestDist) { best = p; bestDist = d; bestVec = mop.hitVec; }
                }
            }

            if (best != null) {
                mc.objectMouseOver = new net.minecraft.util.MovingObjectPosition(best, bestVec);
            }
        }
    }

    @Override
    public void onDisable() {
        // Không cần restore vì không còn thay đổi entity
        originals.clear();
    }
}
