package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.lang.reflect.Method;

public class BlockHitMod extends Module {

    private static final Method RIGHT_CLICK = ReflectionUtil.findMethod(
            Minecraft.class, "rightClickMouse", "func_147121_ag");

    public BlockHitMod() { super("BlockHit"); }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        if (!(packet instanceof C02PacketUseEntity) || RIGHT_CLICK == null) return false;
        if (((C02PacketUseEntity) packet).getAction() != C02PacketUseEntity.Action.ATTACK) return false;
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack == null) return false;
        if (!(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemAxe)) return false;
        mc.addScheduledTask(() -> ReflectionUtil.invoke(RIGHT_CLICK, mc));
        return false;
    }
}
