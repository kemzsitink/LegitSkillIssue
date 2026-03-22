package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class VelocityMod extends Module {
    public VelocityMod() {
        super("Velocity");
    }

    @Override
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) {
        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity vel = (S12PacketEntityVelocity) packet;
            if (vel.getEntityID() == mc.thePlayer.getEntityId()) {
                // Professional approach: Cancel packet or Jump Reset
                // Canceling packet prevents rubberbanding because server doesn't 
                // expect specific velocity movement if the packet itself is dropped in client pipeline.
                
                // For a legit "Jump Reset" approach that complies with strict servers:
                // mc.thePlayer.jump();
                // return false; 
                
                return true; // Cancel the velocity packet entirely (0% KB)
            }
        }
        if (packet instanceof S27PacketExplosion) {
            return true; // Cancel explosion velocity
        }
        return false;
    }
}
