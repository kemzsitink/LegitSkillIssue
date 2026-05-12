package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * REFACTORED: Flight (Advanced/Bypass)
 * 
 * FIXES:
 * - Friction corrected to 0.91 (Air Friction).
 * - Smooth vertical momentum.
 */
public class FlightMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Flight mode", "Motion", "Vanilla", "Damage", "Collision"));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Flight Speed", 0.1f, 5.0f, 0.1f, 1.0f));
    public final NumberSetting vSpeed = addSetting(new NumberSetting("Vertical", "Vertical Speed", 0.1f, 2.0f, 0.1f, 0.5f));

    public FlightMod() {
        super("Flight", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer == null) return;
        if (mode.getMode().equals("Damage")) {
            damagePlayer();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.isFlying = false;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || !event.isPre()) return;

        String currentMode = mode.getMode();

        if (currentMode.equalsIgnoreCase("Vanilla")) {
            mc.thePlayer.capabilities.isFlying = true;
            return;
        }

        mc.thePlayer.capabilities.isFlying = false;

        if (currentMode.equalsIgnoreCase("Collision")) {
            mc.thePlayer.onGround = true;
            mc.thePlayer.motionY = 0.0;
        } else {
            mc.thePlayer.motionY = 0;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY += vSpeed.getValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY -= vSpeed.getValue();
        }

        if (MovementUtils.isMoving()) {
            float strafe = mc.thePlayer.movementInput.moveStrafe;
            float forward = mc.thePlayer.movementInput.moveForward;
            float yaw = mc.thePlayer.rotationYaw;

            // Correct air friction (0.91)
            float friction = (float) (speed.getValue() * 0.15f);
            double[] delta = MovementUtils.calcMoveFlyingDelta(strafe, forward, friction, yaw);

            mc.thePlayer.motionX *= MovementUtils.HORIZONTAL_FRICTION;
            mc.thePlayer.motionZ *= MovementUtils.HORIZONTAL_FRICTION;
            mc.thePlayer.motionX += delta[0];
            mc.thePlayer.motionZ += delta[1];
        } else {
            mc.thePlayer.motionX *= MovementUtils.HORIZONTAL_FRICTION;
            mc.thePlayer.motionZ *= MovementUtils.HORIZONTAL_FRICTION;
        }
    }

    private void damagePlayer() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        for (int i = 0; i < 65; i++) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.049, z, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
    }
}
