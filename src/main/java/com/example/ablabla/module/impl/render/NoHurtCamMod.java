package com.example.ablabla.module.impl.render;

import com.example.ablabla.module.Module;

public class NoHurtCamMod extends Module {

    public NoHurtCamMod() {
        super("NoHurtCam");
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
