package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.lang.reflect.Field;

public class VelocityMod extends Module {

    public int hPercent = 0;  // 0 = no KB, 100 = full KB
    public int vPercent = 0;

    // S12 fields: dev name → SRG name
    private static final Field S12_X = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionX", "field_149069_b");
    private static final Field S12_Y = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionY", "field_149068_c");
    private static final Field S12_Z = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionZ", "field_149067_d");

    // S27 fields: dev name → SRG name (field_149159_h is motionZ)
    private static final Field S27_X = ReflectionUtil.findField(S27PacketExplosion.class, "field_149152_f");
    private static final Field S27_Y = ReflectionUtil.findField(S27PacketExplosion.class, "field_149153_g");
    private static final Field S27_Z = ReflectionUtil.findField(S27PacketExplosion.class, "field_149159_h");

    public VelocityMod() {
        super("Velocity");
    }

    @Override
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) {
        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity vel = (S12PacketEntityVelocity) packet;
            if (mc.thePlayer == null || vel.getEntityID() != mc.thePlayer.getEntityId()) return false;

            if (hPercent == 0 && vPercent == 0) return true; // cancel entirely

            if (S12_X == null || S12_Y == null || S12_Z == null) return true; // fields not found, cancel

            try {
                int x = S12_X.getInt(vel);
                int y = S12_Y.getInt(vel);
                int z = S12_Z.getInt(vel);
                S12_X.setInt(vel, (int)(x * hPercent / 100.0));
                S12_Y.setInt(vel, (int)(y * vPercent / 100.0));
                S12_Z.setInt(vel, (int)(z * hPercent / 100.0));
            } catch (Exception e) {
                return true; // fallback cancel
            }
            return false;
        }

        if (packet instanceof S27PacketExplosion) {
            if (hPercent == 0 && vPercent == 0) return true;

            if (S27_X == null || S27_Y == null || S27_Z == null) return true;

            try {
                float x = S27_X.getFloat(packet);
                float y = S27_Y.getFloat(packet);
                float z = S27_Z.getFloat(packet);
                S27_X.setFloat(packet, x * hPercent / 100f);
                S27_Y.setFloat(packet, y * vPercent / 100f);
                S27_Z.setFloat(packet, z * hPercent / 100f);
            } catch (Exception e) {
                return true;
            }
            return false;
        }

        return false;
    }
}
