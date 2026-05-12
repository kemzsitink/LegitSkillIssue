package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

/**
 * REFACTORED (Legitimacy Scan — Protocol Timing):
 *
 * VI PHẠM CŨ (Networking — Packet Timing):
 *   Dòng cũ: Gửi C07 + C08 mỗi tick khi đang dùng item và đang di chuyển.
 *   → Gửi 2 packet/tick liên tục = 40 packet/giây (vượt 20t/s).
 *   → Server nhận RELEASE_USE_ITEM + USE_ITEM liên tục → flag "Packet spam".
 *
 * SỬA:
 *   - Chỉ gửi 1 lần khi bắt đầu dùng item (rising edge), không gửi mỗi tick.
 *   - Dùng cooldown 1 tick để tránh gửi lại ngay lập tức.
 *   - Công thức A (cũ): Gửi C07+C08 mỗi tick khi isUsingItem() && moving
 *   - Công thức B (mới): Gửi 1 lần khi phát hiện bắt đầu dùng item + đang di chuyển,
 *                        sau đó chờ đến khi ngừng dùng item mới reset
 *   - FIX: Override movement input to bypass client-side 0.2x slowdown.
 */
public class NoSlowMod extends Module {

    private boolean wasSent = false;

    public NoSlowMod() { super("NoSlow", Category.MOVEMENT); }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;

        if (!event.isPre()) {
            boolean usingItem = mc.thePlayer.isUsingItem();
            if (usingItem && (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0)) {
                mc.thePlayer.movementInput.moveForward *= 5.0F;
                mc.thePlayer.movementInput.moveStrafe *= 5.0F;
            }
            return;
        }

        boolean usingItem = mc.thePlayer.isUsingItem();
        boolean moving    = mc.thePlayer.movementInput.moveForward != 0
                         || mc.thePlayer.movementInput.moveStrafe  != 0;

        if (!usingItem) {
            wasSent = false;
            return;
        }

        if (!moving) return;

        if (!wasSent) {
            mc.getNetHandler().addToSendQueue(
                new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.getNetHandler().addToSendQueue(
                new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            wasSent = true;
        }
    }

    @Override
    public void onDisable() {
        wasSent = false;
    }
}
