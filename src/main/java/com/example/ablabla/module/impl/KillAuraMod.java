package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KillAuraMod extends Module {

    public float range    = 4.0f;
    public float rotSpeed = 0.8f; // how fast to snap rotation (0-1)

    private long lastAttack = 0;
    private static final long ATTACK_DELAY_MS = 60; // ~16 CPS max

    public KillAuraMod() {
        super("KillAura");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        List<EntityPlayer> targets = getTargets();
        if (targets.isEmpty()) return;

        // Pick closest valid target
        EntityPlayer target = null;
        double best = Double.MAX_VALUE;
        for (EntityPlayer p : targets) {
            double d = mc.thePlayer.getDistanceSqToEntity(p);
            if (d < best) { best = d; target = p; }
        }
        if (target == null) return;

        // Smooth rotation toward target
        rotateToward(target);

        // Attack on cooldown
        long now = System.currentTimeMillis();
        if (now - lastAttack < ATTACK_DELAY_MS) return;
        lastAttack = now;

        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();
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

    private void rotateToward(EntityPlayer target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dz = target.posZ - mc.thePlayer.posZ;
        double dy = (target.posY + target.getEyeHeight() * 0.8)
                  - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw   = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)-(Math.atan2(dy, dist) * 180.0 / Math.PI);

        float yawDiff   = wrapAngle(yaw   - mc.thePlayer.rotationYaw);
        float pitchDiff = wrapAngle(pitch - mc.thePlayer.rotationPitch);

        mc.thePlayer.rotationYaw   += yawDiff   * rotSpeed;
        mc.thePlayer.rotationPitch += pitchDiff * rotSpeed;
    }

    private float wrapAngle(float a) {
        while (a > 180)  a -= 360;
        while (a < -180) a += 360;
        return a;
    }
}
