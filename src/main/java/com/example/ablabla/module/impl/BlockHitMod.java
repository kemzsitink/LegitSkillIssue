package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import java.lang.reflect.Method;

public class BlockHitMod extends Module {
    private Method rightClickMethod;

    public BlockHitMod() {
        super("BlockHit");
        try {
            // "rightClickMouse" in dev environment
            rightClickMethod = Minecraft.class.getDeclaredMethod("rightClickMouse");
        } catch (Exception e) {
            try {
                // "func_147121_ag" is the SRG name for rightClickMouse in 1.8.9
                rightClickMethod = Minecraft.class.getDeclaredMethod("func_147121_ag");
            } catch (Exception ex) {}
        }
        if (rightClickMethod != null) {
            rightClickMethod.setAccessible(true);
        }
    }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        if (packet instanceof C02PacketUseEntity && rightClickMethod != null) {
            C02PacketUseEntity useEntity = (C02PacketUseEntity) packet;
            
            // Nếu là gói tin tấn công (ATTACK)
            if (useEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
                
                // Nếu đang cầm kiếm
                if (stack != null && stack.getItem() instanceof ItemSword) {
                    try {
                        // Tự động kích hoạt chuột phải ngay sau khi chém
                        rightClickMethod.invoke(mc);
                    } catch (Exception e) {}
                }
            }
        }
        return false;
    }
}
