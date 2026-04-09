package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class HitBoxMod extends Module {

    public float expand = 0.2f;

    public HitBoxMod() {
        super("HitBox");
    }

    @Override
    public void onTick() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        // Copy list to avoid ConcurrentModificationException from Netty thread
        EntityPlayer[] snapshot = mc.theWorld.playerEntities.toArray(new EntityPlayer[0]);
        for (EntityPlayer p : snapshot) {
            if (p == null || p == mc.thePlayer || p.isDead) continue;
            if (p.getEntityBoundingBox() == null) continue;
            expand(p, expand);
        }
    }

    @Override
    public void onDisable() {
        if (mc.theWorld == null) return;
        EntityPlayer[] snapshot = mc.theWorld.playerEntities.toArray(new EntityPlayer[0]);
        for (EntityPlayer p : snapshot) {
            if (p == null || p == mc.thePlayer) continue;
            if (p.getEntityBoundingBox() == null) continue;
            expand(p, 0.0f);
        }
    }

    private void expand(EntityPlayer p, float extra) {
        try {
            float w = 0.6f + extra;
            float h = 1.8f + extra;
            p.width  = w;
            p.height = h;
            p.setEntityBoundingBox(new AxisAlignedBB(
                p.posX - w / 2f, p.posY,     p.posZ - w / 2f,
                p.posX + w / 2f, p.posY + h, p.posZ + w / 2f
            ));
        } catch (Exception ignored) {}
    }
}
