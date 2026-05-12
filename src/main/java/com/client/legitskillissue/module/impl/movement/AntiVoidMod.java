package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

public class AntiVoidMod extends Module {

    public final NumberSetting distance = addSetting(new NumberSetting("Distance", "Fall distance to trigger", 3f, 15f, 1f, 5f));

    public AntiVoidMod() {
        super("AntiVoid", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre() || mc.thePlayer == null) return;

        if (mc.thePlayer.fallDistance > distance.getValue() && !isBlockUnder()) {
            // Give a massive upward boost to save the player
            mc.thePlayer.motionY = 1.0;
            mc.thePlayer.fallDistance = 0;
        }
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) return false;
        for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }
}
