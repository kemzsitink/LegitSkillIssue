package com.example.ablabla.module.impl.combat;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.lang.reflect.Field;

public class VelocityMod extends Module {

    public final NumberSetting hPercent = addSetting(new NumberSetting("H-Velocity", "Horizontal KB%", 0f, 100f, 5f, 50f));
    public final NumberSetting vPercent = addSetting(new NumberSetting("V-Velocity", "Vertical KB%",   0f, 100f, 5f, 100f));

    private static final Field S12_X = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionX", "field_149069_b");
    private static final Field S12_Y = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionY", "field_149068_c");
    private static final Field S12_Z = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionZ", "field_149067_d");
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
            if (S12_X == null || S12_Y == null || S12_Z == null) return false;
            try {
                S12_X.setInt(vel, (int)(S12_X.getInt(vel) * hPercent.getInt() / 100.0));
                S12_Y.setInt(vel, (int)(S12_Y.getInt(vel) * vPercent.getInt() / 100.0));
                S12_Z.setInt(vel, (int)(S12_Z.getInt(vel) * hPercent.getInt() / 100.0));
            } catch (Exception ignored) {}
            return false;
        }
        if (packet instanceof S27PacketExplosion) {
            if (S27_X == null || S27_Y == null || S27_Z == null) return false;
            try {
                S27_X.setFloat(packet, S27_X.getFloat(packet) * hPercent.getInt() / 100f);
                S27_Y.setFloat(packet, S27_Y.getFloat(packet) * vPercent.getInt() / 100f);
                S27_Z.setFloat(packet, S27_Z.getFloat(packet) * hPercent.getInt() / 100f);
            } catch (Exception ignored) {}
            return false;
        }
        return false;
    }
}
