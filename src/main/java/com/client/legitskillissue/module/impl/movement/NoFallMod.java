package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * REFACTORED (MCP-919): NoFall — Physics-Compliant Fall Damage Prevention
 *
 * VI PHẠM CŨ:
 *   Dòng cũ (NoFallMod.java:24-28): ON_GROUND.setBoolean(packet, true) qua reflection
 *   → Set onGround=true trong packet mà không kiểm tra AABB thực, vi phạm chỉ thị §1.
 *   Dòng cũ (NoFallMod.java:31): mc.thePlayer.fallDistance = 0.0f
 *   → Reset fallDistance thủ công, vi phạm chỉ thị §1 (không được can thiệp biến
 *     mà không qua hệ thống tính toán của game).
 *
 * THAY ĐỔI:
 *   - Bỏ hoàn toàn reflection và can thiệp packet.
 *   - Khi fallDistance >= 3.0f VÀ người chơi thực sự sắp chạm đất (AABB check),
 *     gọi mc.thePlayer.motionY = 0 để dừng rơi — engine sẽ tự xử lý onGround
 *     và fallDistance đúng cách ở tick tiếp theo.
 *   - Nếu không sắp chạm đất, không làm gì — tránh false positive.
 *
 *   Công thức A (cũ): ON_GROUND.setBoolean(packet, true); fallDistance = 0;
 *   Công thức B (mới): Kiểm tra AABB bên dưới; nếu sắp chạm đất thì motionY = 0
 *                      để engine tự xử lý onGround đúng cách.
 */
public class NoFallMod extends Module {

    public NoFallMod() {
        super("NoFall", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Chỉ can thiệp khi đang rơi đủ ngưỡng gây sát thương
        if (mc.thePlayer.fallDistance < 3.0f) return;
        if (mc.thePlayer.onGround) return;

        // THAY ĐỔI: Kiểm tra AABB thực theo chỉ thị §1
        // Công thức A (cũ): ON_GROUND.setBoolean(packet, true); fallDistance = 0;
        // Công thức B (mới): Dò AABB bên dưới; nếu sắp chạm đất thì set motionY=0
        //   để engine tự xử lý onGround và reset fallDistance đúng cách.
        if (isAboutToLand()) {
            // Dừng rơi — engine sẽ detect onGround = true ở tick tiếp theo
            // thông qua moveEntity() → isCollidedVertically && d4 < 0
            mc.thePlayer.motionY = 0;
        }
    }

    /**
     * Kiểm tra xem người chơi có sắp chạm đất trong tick tiếp theo không,
     * bằng cách dò AABB tại vị trí Y + motionY.
     * Theo chỉ thị §1: dùng worldObj.getCollidingBoundingBoxes.
     */
    private boolean isAboutToLand() {
        AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        double nextY = mc.thePlayer.motionY;
        // Dò AABB tại vị trí sau khi áp dụng motionY (+ thêm 0.1 buffer)
        AxisAlignedBB checkBB = new AxisAlignedBB(
                playerBB.minX, playerBB.minY + nextY - 0.1D, playerBB.minZ,
                playerBB.maxX, playerBB.minY,                 playerBB.maxZ);
        List<?> collisions = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, checkBB);
        return !collisions.isEmpty();
    }
}
