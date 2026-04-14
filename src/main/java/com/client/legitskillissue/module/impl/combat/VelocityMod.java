package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.lang.reflect.Field;

/**
 * MAPPING FIX (fields.csv / MCP-919):
 *
 * S12PacketEntityVelocity:
 *   Obfuscated cũ (SAI): field_149069_b, field_149068_c, field_149067_d
 *   → Không tồn tại trong fields.csv. field_149068_f = pitch (S14PacketEntity), không liên quan.
 *   Obfuscated đúng (fields.csv):
 *     field_149415_b = motionX
 *     field_149416_c = motionY
 *     field_149414_d = motionZ
 *     field_149417_a = entityID
 *
 * S27PacketExplosion:
 *   field_149152_f, field_149153_g, field_149159_h KHÔNG có trong fields.csv
 *   → Vẫn còn obfuscated trong vanilla 1.8.9 (chưa được map).
 *   → Dùng getter thay vì reflection:
 *     func_149149_c() = motionX (player velocity X sau explosion)
 *     func_149144_d() = motionY
 *     func_149147_e() = motionZ
 *   → Vì S27PacketExplosion không có setter, dùng reflection với tên obfuscated gốc.
 */
public class VelocityMod extends Module {

    public final NumberSetting hPercent = addSetting(new NumberSetting("H-Velocity", "Horizontal KB%", 0f, 100f, 5f, 50f));
    public final NumberSetting vPercent = addSetting(new NumberSetting("V-Velocity", "Vertical KB%",   0f, 100f, 5f, 100f));

    // MAPPING FIX: Obfuscated đúng theo fields.csv
    private static final Field S12_X = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionX", "field_149415_b");
    private static final Field S12_Y = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionY", "field_149416_c");
    private static final Field S12_Z = ReflectionUtil.findField(S12PacketEntityVelocity.class, "motionZ", "field_149414_d");

    // S27: field_149152_f/g/h chưa được map trong fields.csv — dùng tên obfuscated gốc
    // (không có tên MCP, không có getter setter — phải dùng reflection với tên obfuscated)
    private static final Field S27_X = ReflectionUtil.findField(S27PacketExplosion.class, "field_149152_f");
    private static final Field S27_Y = ReflectionUtil.findField(S27PacketExplosion.class, "field_149153_g");
    private static final Field S27_Z = ReflectionUtil.findField(S27PacketExplosion.class, "field_149159_h");

    public VelocityMod() {
        super("Velocity", Category.COMBAT);
    }

    @Override
    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) {
        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity vel = (S12PacketEntityVelocity) packet;
            if (mc.thePlayer == null || vel.getEntityID() != mc.thePlayer.getEntityId()) return false;
            if (S12_X == null || S12_Y == null || S12_Z == null) return false;
            try {
                // MAPPING FIX: Dùng field_149415_b/416_c/414_d thay vì field_149069_b/068_c/067_d
                S12_X.setInt(vel, (int)(S12_X.getInt(vel) * hPercent.getInt() / 100.0));
                S12_Y.setInt(vel, (int)(S12_Y.getInt(vel) * vPercent.getInt() / 100.0));
                S12_Z.setInt(vel, (int)(S12_Z.getInt(vel) * hPercent.getInt() / 100.0));
            } catch (Exception ignored) {}
            return false;
        }
        if (packet instanceof S27PacketExplosion) {
            // S27: field_149152/153/159 chưa được map — dùng reflection với tên obfuscated gốc
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
