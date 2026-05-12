package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.entity.Entity;
import java.lang.reflect.Field;

public class NoWebMod extends Module {

    private static final Field IS_IN_WEB = ReflectionUtil.findField(Entity.class, "isInWeb", "field_70134_J");

    public NoWebMod() {
        super("NoWeb", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null) return;
        
        try {
            if (IS_IN_WEB != null) {
                boolean inWeb = IS_IN_WEB.getBoolean(mc.thePlayer);
                if (inWeb) {
                    IS_IN_WEB.setBoolean(mc.thePlayer, false);
                    mc.thePlayer.motionY = -0.1; // Fall slowly through it or allow jumping
                }
            }
        } catch (Exception e) {}
    }
}
