package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class NoHurtCamMod extends Module {

    public NoHurtCamMod() {
        super("NoHurtCam", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;
        // Suppress hurt camera tilt and red tint
        if (mc.thePlayer.hurtTime > 0) {
            mc.thePlayer.hurtTime = 0;
            mc.thePlayer.maxHurtTime = 0;
            mc.thePlayer.attackedAtYaw = 0;
        }
    }
}
