package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class FreecamModule extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1);
    private Vec3d oldPos;

    public FreecamModule() {
        super("Freecam", "Free movement of the camera.", Category.RENDER);
        addSetting(speed);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            oldPos = mc.player.getPos();
            mc.player.noClip = true;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && oldPos != null) {
            mc.player.setPosition(oldPos);
            mc.player.noClip = false;
            mc.player.setVelocity(0, 0, 0);
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed((float) (speed.getValue() * 0.05f));
    }
}
