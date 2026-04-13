package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;

public class FlightMod extends Module {

    public FlightMod() {
        super("Flight");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;
        // Force flight on every tick
        mc.thePlayer.capabilities.isFlying = true;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        // Restore normal state if not in creative mode
        if (!mc.thePlayer.capabilities.isCreativeMode) {
            mc.thePlayer.capabilities.isFlying = false;
        }
    }
}
