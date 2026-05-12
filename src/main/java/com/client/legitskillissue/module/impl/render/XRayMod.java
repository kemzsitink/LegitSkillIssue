package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class XRayMod extends Module {

    public XRayMod() {
        super("XRay", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    // XRay logic requires mixins/ASM into Block#shouldSideBeRendered and Block#getRenderBlockPass
    // Since we can't inject mixins here directly without modifying LaunchWrapper, 
    // Fullbright + CaveFinder approach is a poor man's XRay.
    // For a real XRay, we'd add ASM hooks. We leave this as a stub that triggers chunk reload.
}
