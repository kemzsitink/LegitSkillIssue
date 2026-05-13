package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.*;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RotationUtils;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REFACTORED: AimAssist (Legit/Closet)
 * 
 * Improvements:
 * - Uses RotationUtils for smooth, GCD-compliant movements.
 * - Targeted filtering (Distance, FOV).
 * - Only assists when attacking or holding down a mouse button (Configurable).
 */
public class AimAssistMod extends Module {

    public final NumberSetting fov = addSetting(new NumberSetting("FOV", "Field of view for assist", 10f, 180f, 1f, 45f));
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Smoothness speed", 1f, 100f, 1f, 15f));
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Max distance to target", 3f, 8f, 0.1f, 4.5f));
    public final BooleanSetting clickOnly = addSetting(new BooleanSetting("Click Only", "Only assist when clicking", true));

    public AimAssistMod() {
        super("AimAssist", Category.MISC);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!event.isPre()) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;
        if (clickOnly.getValue() && !Mouse.isButtonDown(0)) return;

        // Find best target based on FOV and Distance
        List<EntityPlayer> targets = mc.theWorld.playerEntities.stream()
                .filter(e -> e != mc.thePlayer && !e.isDead && e.getDistanceToEntity(mc.thePlayer) <= range.getValue())
                .filter(e -> !com.client.legitskillissue.utils.FriendManager.isFriend(e.getName())) // Filter friends
                .filter(e -> RotationUtils.isInFov(e, fov.getValue()))
                .sorted(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceToEntity(e)))
                .collect(Collectors.toList());

        if (targets.isEmpty()) return;

        EntityPlayer target = targets.get(0);

        // Calculate rotations to target's upper body/head area
        float[] targetRots = RotationUtils.getRotations(
                mc.thePlayer.getPositionEyes(1.0f),
                target.getPositionEyes(1.0f).subtract(0, 0.1, 0) // Aim slightly below eyes
        );

        // Apply smooth rotation
        float[] smoothed = RotationUtils.getSmoothRotations(
                new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch},
                targetRots,
                speed.getValue()
        );

        // Apply to player
        mc.thePlayer.rotationYaw = smoothed[0];
        mc.thePlayer.rotationPitch = smoothed[1];
    }
}
