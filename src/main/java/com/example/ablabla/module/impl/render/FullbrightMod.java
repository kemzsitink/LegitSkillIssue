package com.example.ablabla.module.impl.render;

import com.example.ablabla.module.Module;

public class FullbrightMod extends Module {

    private float savedGamma = 1.0f;

    public FullbrightMod() { super("Fullbright"); }

    @Override
    protected void onEnable() {
        if (mc.gameSettings != null) {
            savedGamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100.0f;
        }
    }

    @Override
    public void onTick() {
        if (mc.gameSettings != null) mc.gameSettings.gammaSetting = 100.0f;
    }

    @Override
    protected void onDisable() {
        if (mc.gameSettings != null) mc.gameSettings.gammaSetting = savedGamma;
    }
}
