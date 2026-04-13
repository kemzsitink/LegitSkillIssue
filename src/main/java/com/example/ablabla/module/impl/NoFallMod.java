package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.lang.reflect.Field;

public class NoFallMod extends Module {

    private static final Field ON_GROUND = ReflectionUtil.findField(C03PacketPlayer.class, "onGround", "field_149474_g");

    public NoFallMod() {
        super("NoFall");
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (mc.thePlayer == null || !isEnabled()) return false;

        if (packet instanceof C03PacketPlayer) {
            // Đợi đến khi rơi đủ 3 block (chuẩn bị mất máu) mới can thiệp
            if (mc.thePlayer.fallDistance >= 3.0f) {
                try {
                    if (ON_GROUND != null) {
                        // Báo cho Server biết mình vừa chạm đất ảo
                        ON_GROUND.setBoolean(packet, true);
                    }
                    
                    // QUAN TRỌNG NHẤT: Bắt buộc phải reset fallDistance ở Máy khách (Client)
                    // Nếu không reset, nó sẽ spam liên tục ở các tick sau gây ra lỗi "Moved wrongly!"
                    mc.thePlayer.fallDistance = 0.0f;
                    
                } catch (Exception ignored) {}
            }
        }
        return false;
    }
}
