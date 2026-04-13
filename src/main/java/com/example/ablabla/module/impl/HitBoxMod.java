package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.setting.NumberSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.IdentityHashMap;
import java.util.Map;

public class HitBoxMod extends Module {

    public final NumberSetting expand = addSetting(new NumberSetting("HitBox", "Expand size", 0f, 0.5f, 0.05f, 0.2f));

    private final Map<EntityPlayer, float[]> originals = new IdentityHashMap<>();

    public HitBoxMod() { super("HitBox"); }

    @Override
    public void onTick() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        for (EntityPlayer p : mc.theWorld.playerEntities.toArray(new EntityPlayer[0])) {
            if (p == null || p == mc.thePlayer || p.isDead) continue;
            if (!originals.containsKey(p)) originals.put(p, new float[]{ p.width, p.height });
            float[] orig = originals.get(p);
            float w = orig[0] + expand.getValue();
            float h = orig[1] + expand.getValue();
            p.width = w; p.height = h;
            p.setEntityBoundingBox(new AxisAlignedBB(
                p.posX - w/2f, p.posY, p.posZ - w/2f,
                p.posX + w/2f, p.posY + h, p.posZ + w/2f));
        }
    }

    @Override
    public void onDisable() {
        if (mc.theWorld != null)
            for (EntityPlayer p : mc.theWorld.playerEntities.toArray(new EntityPlayer[0])) {
                float[] orig = originals.get(p);
                if (orig == null || p == mc.thePlayer) continue;
                p.width = orig[0]; p.height = orig[1];
                p.setEntityBoundingBox(new AxisAlignedBB(
                    p.posX - orig[0]/2f, p.posY, p.posZ - orig[0]/2f,
                    p.posX + orig[0]/2f, p.posY + orig[1], p.posZ + orig[0]/2f));
            }
        originals.clear();
    }
}
