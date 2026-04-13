package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.lang.reflect.Field;
import java.util.Random;

public class SilentAuraMod extends Module {

    public final NumberSetting range    = addSetting(new NumberSetting("SA Range",    "Attack range",           2.0f, 6.0f, 0.1f, 4.0f));
    public final NumberSetting minDelay = addSetting(new NumberSetting("SA MinDelay", "Min ticks between hits", 2f,   10f,  1f,   4f));
    public final NumberSetting maxDelay = addSetting(new NumberSetting("SA MaxDelay", "Max ticks between hits", 2f,   10f,  1f,   7f));

    private static final Field YAW      = ReflectionUtil.findField(C03PacketPlayer.class, "yaw",      "field_149476_e");
    private static final Field PITCH    = ReflectionUtil.findField(C03PacketPlayer.class, "pitch",    "field_149473_f");
    private static final Field ROTATING = ReflectionUtil.findField(C03PacketPlayer.class, "rotating", "field_149481_i");

    private EntityPlayer target;
    private float spoofYaw, spoofPitch;
    private int ticksUntilAttack = 0;
    private final Random rng = new Random();

    public SilentAuraMod() { super("SilentAura"); }

    @Override
    protected void onEnable() { target = null; ticksUntilAttack = nextDelay(); }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        target = getClosestTarget();
        if (target == null) return;

        float[] rots = calcRotation(target);
        spoofYaw   = rots[0];
        spoofPitch = rots[1];

        ticksUntilAttack--;
        if (ticksUntilAttack > 0) return;
        ticksUntilAttack = nextDelay();
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, target);
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (target != null && packet instanceof C03PacketPlayer
                && YAW != null && PITCH != null && ROTATING != null) {
            try {
                YAW.setFloat(packet, spoofYaw);
                PITCH.setFloat(packet, spoofPitch);
                ROTATING.setBoolean(packet, true);
            } catch (Exception ignored) {}
        }
        return false;
    }

    private EntityPlayer getClosestTarget() {
        EntityPlayer closest = null;
        float rangeSq = range.getValue() * range.getValue();
        double best = Double.MAX_VALUE;
        for (EntityPlayer p : mc.theWorld.playerEntities.toArray(new EntityPlayer[0])) {
            if (p == mc.thePlayer || p.isDead) continue;
            double d = mc.thePlayer.getDistanceSqToEntity(p);
            if (d < rangeSq && d < best) { best = d; closest = p; }
        }
        return closest;
    }

    private float[] calcRotation(EntityPlayer t) {
        double dx = t.posX - mc.thePlayer.posX;
        double dz = t.posZ - mc.thePlayer.posZ;
        double dy = (t.posY + t.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);
        return new float[]{ yaw, pitch };
    }

    private int nextDelay() {
        return minDelay.getInt() + rng.nextInt(Math.max(1, maxDelay.getInt() - minDelay.getInt() + 1));
    }
}
