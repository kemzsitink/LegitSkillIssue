package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class FullbrightMod extends Module {

    private float savedGamma = 1.0f;

    public FullbrightMod() { super("Fullbright", Category.RENDER); }

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
