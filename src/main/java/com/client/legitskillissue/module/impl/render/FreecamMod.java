package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FreecamMod extends Module {

    private EntityOtherPlayerMP clone;
    private double oldX, oldY, oldZ;
    private float oldYaw, oldPitch;

    public FreecamMod() {
        super("Freecam", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer == null) return;

        oldX = mc.thePlayer.posX;
        oldY = mc.thePlayer.posY;
        oldZ = mc.thePlayer.posZ;
        oldYaw = mc.thePlayer.rotationYaw;
        oldPitch = mc.thePlayer.rotationPitch;

        clone = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        clone.copyLocationAndAnglesFrom(mc.thePlayer);
        clone.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-100, clone);

        mc.thePlayer.noClip = true;
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer == null) return;
        
        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, oldYaw, oldPitch);
        mc.thePlayer.noClip = false;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        
        if (clone != null) {
            mc.theWorld.removeEntityFromWorld(-100);
            clone = null;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = true;
            mc.thePlayer.motionY = 0;
            if (mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY += 1.0;
            if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.thePlayer.motionY -= 1.0;
            mc.thePlayer.capabilities.isFlying = true;
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.isSend && event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true); // Don't send movement to server
        }
    }
}
