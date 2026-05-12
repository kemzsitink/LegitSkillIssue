package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Random;

public class DerpMod extends Module {

    private final Random random = new Random();
    private float currentYaw;

    public DerpMod() {
        super("Derp", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre() && mc.thePlayer != null) {
            currentYaw += (random.nextFloat() * 100) + 50;
            if (currentYaw > 360) currentYaw -= 360;
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.isSend && event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
            
            // Random pitch between 90 and -90, spinning yaw
            float pitch = random.nextBoolean() ? 90f : -90f;

            if (packet.isMoving()) {
                event.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), 
                    currentYaw, pitch, packet.isOnGround()
                ));
            } else {
                event.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(
                    currentYaw, pitch, packet.isOnGround()
                ));
            }
        }
    }
}
