package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KillAuraMod extends Module {

    public float range = 4.0f;
    // Attack every N ticks (1 tick = 50ms). 1 = max speed (~20 CPS theoretical)
    public int attackDelay = 1;

    private int ticksSinceAttack = 0;

    public KillAuraMod() {
        super("KillAura");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        ticksSinceAttack++;

        List<EntityPlayer> targets = getTargets();
        if (targets.isEmpty()) return;

        EntityPlayer target = null;
        double best = Double.MAX_VALUE;
        for (EntityPlayer p : targets) {
            double d = mc.thePlayer.getDistanceSqToEntity(p);
            if (d < best) { best = d; target = p; }
        }
        if (target == null) return;

        // Aim at top of hitbox — server doesn't validate Y aim strictly
        float[] rot = calcRotation(target, true);

        float savedYaw   = mc.thePlayer.rotationYaw;
        float savedPitch = mc.thePlayer.rotationPitch;

        mc.thePlayer.rotationYaw   = rot[0];
        mc.thePlayer.rotationPitch = rot[1];

        if (ticksSinceAttack >= attackDelay) {
            ticksSinceAttack = 0;
            mc.playerController.attackEntity(mc.thePlayer, target);
            mc.thePlayer.swingItem();
        }

        mc.thePlayer.rotationYaw   = savedYaw;
        mc.thePlayer.rotationPitch = savedPitch;
    }

    private List<EntityPlayer> getTargets() {
        List<EntityPlayer> list = new ArrayList<>();
        EntityPlayer[] snapshot = mc.theWorld.playerEntities.toArray(new EntityPlayer[0]);

        for (EntityPlayer p : snapshot) {
            if (p == null || p == mc.thePlayer || p.isDead) continue;
            if (mc.thePlayer.getDistanceToEntity(p) > range) continue;
            if (isBot(p)) continue;
            if (isTeammate(p)) continue;
            list.add(p);
        }
        return list;
    }

    /**
     * Bot detection:
     * Bots wear gold armor in the chest slot (armorInventory[2])
     * and gold armor cannot be dyed — so it's always default color.
     * Real players typically wear leather (colored) or iron/diamond.
     */
    private boolean isBot(EntityPlayer p) {
        // armorInventory: 0=boots, 1=legs, 2=chest, 3=helmet
        ItemStack chest = p.inventory.armorInventory[2];
        if (chest == null) return false;
        if (!(chest.getItem() instanceof ItemArmor)) return false;

        ItemArmor armor = (ItemArmor) chest.getItem();
        // Gold chestplate = bot indicator
        return armor.getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD;
    }

    /**
     * Team detection:
     * Check if target's chestplate is leather AND same color as our chestplate.
     * Same color = same team in BedWars/SkyWars.
     */
    private boolean isTeammate(EntityPlayer p) {
        // First check Minecraft scoreboard team
        if (mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(p.getTeam())) {
            return true;
        }

        // Fallback: leather armor color check
        ItemStack myChest = mc.thePlayer.inventory.armorInventory[2];
        ItemStack theirChest = p.inventory.armorInventory[2];

        if (myChest == null || theirChest == null) return false;
        if (!(myChest.getItem() instanceof ItemArmor)) return false;
        if (!(theirChest.getItem() instanceof ItemArmor)) return false;

        ItemArmor myArmor    = (ItemArmor) myChest.getItem();
        ItemArmor theirArmor = (ItemArmor) theirChest.getItem();

        // Only leather can be dyed
        if (myArmor.getArmorMaterial()    != ItemArmor.ArmorMaterial.LEATHER) return false;
        if (theirArmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER) return false;

        int myColor    = myArmor.getColor(myChest);
        int theirColor = theirArmor.getColor(theirChest);

        return myColor == theirColor;
    }

    private float[] calcRotation(EntityPlayer target, boolean aimTop) {
        double dx = target.posX - mc.thePlayer.posX;
        double dz = target.posZ - mc.thePlayer.posZ;

        double targetY;
        if (aimTop) {
            // Aim at top of AABB — server doesn't validate Y hit position
            // This gives max reach vertically since server only checks AABB intersection
            targetY = target.posY + target.height - 0.1;
        } else {
            targetY = target.posY + target.getEyeHeight() * 0.8;
        }

        double dy   = targetY - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);

        // Clamp pitch to valid range
        if (pitch > 90)  pitch = 90;
        if (pitch < -90) pitch = -90;

        return new float[]{ yaw, pitch };
    }

    private float wrapAngle(float a) {
        while (a > 180)  a -= 360;
        while (a < -180) a += 360;
        return a;
    }
}
