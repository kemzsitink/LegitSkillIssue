package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.MovementUtils;

public class SpeedMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Speed mode", "Bhop", "Strafe", "OnGround"));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Speed multiplier", 1.0f, 5.0f, 0.1f, 1.5f));

    public SpeedMod() {
        super("Speed", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!MovementUtils.isMoving()) return;

        String currentMode = mode.getMode();
        double baseSpeed = MovementUtils.getBaseMoveSpeed();

        switch (currentMode) {
            case "Bhop":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MovementUtils.setSpeed(baseSpeed * speed.getValue());
                } else {
                    MovementUtils.setSpeed(MovementUtils.getSpeed() - (MovementUtils.getSpeed() / 159.0));
                }
                break;
            case "Strafe":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                MovementUtils.setSpeed(baseSpeed * speed.getValue() * 0.8);
                break;
            case "OnGround":
                if (mc.thePlayer.onGround) {
                    // Small micro hops or packet-based speed (simulated with motion)
                    mc.thePlayer.motionY = 0.01; 
                    MovementUtils.setSpeed(baseSpeed * speed.getValue() * 1.2);
                }
                break;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.jumpMovementFactor = 0.02f;
        }
    }
}
