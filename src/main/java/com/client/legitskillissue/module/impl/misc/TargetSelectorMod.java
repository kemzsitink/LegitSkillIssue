package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.Logger;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TargetSelector - Centralized targeting system for all combat modules.
 * 
 * FEATURES:
 * - Multiple targeting modes (Closest, Health, Damage, Threat, etc.)
 * - Team detection (Scoreboard, Name Color, Armor)
 * - Priority system (Players > Mobs > Animals)
 * - Switch delay (anti-jitter)
 * - Ignore invisible option
 * - FOV filtering
 * 
 * BENEFITS:
 * - All combat modules use same targeting logic
 * - Consistent behavior across modules
 * - Easy to update targeting for all modules
 * - Reduces code duplication
 */
public class TargetSelectorMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Targeting mode",
        "Closest", "Lowest Health", "Most Damage", "Threat Level", "Team Aware"));
    public final ModeSetting priority = addSetting(new ModeSetting("Priority", "Entity priority",
        "Players Only", "Players > Mobs", "Players > Animals", "All"));
    public final NumberSetting switchDelay = addSetting(new NumberSetting("Switch Delay", "Delay before switching targets (ticks)", 
        0f, 40f, 1f, 10f));
    public final NumberSetting fov = addSetting(new NumberSetting("FOV", "Field of view filter", 
        30f, 360f, 10f, 180f));
    public final BooleanSetting ignoreInvisible = addSetting(new BooleanSetting("Ignore Invisible", "Ignore invisible entities", true));
    public final BooleanSetting ignoreTeam = addSetting(new BooleanSetting("Ignore Team", "Don't target teammates", true));
    public final BooleanSetting antiBot = addSetting(new BooleanSetting("AntiBot", "Use AntiBot filtering", true));

    private static final Logger logger = Logger.getLogger(TargetSelectorMod.class);
    
    // Current target tracking
    private EntityLivingBase currentTarget = null;
    private long lastSwitchTime = 0;
    private final Map<UUID, Integer> damageTracker = new HashMap<>();
    
    // Singleton instance for easy access
    private static TargetSelectorMod instance;

    public TargetSelectorMod() {
        super("TargetSelector", Category.MISC);
        instance = this;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (event.isPre()) {    
            if (mc.thePlayer == null || mc.theWorld == null) return;
    
            // Update damage tracker
            cleanupDamageTracker();
            
            // Update current target
            updateTarget();
                }
    }

    /**
     * Updates the current target based on settings.
     */
    private void updateTarget() {
        long now = System.currentTimeMillis();
        
        // Check switch delay
        if (currentTarget != null && !currentTarget.isDead && 
            now - lastSwitchTime < switchDelay.getValue() * 50) {
            return; // Don't switch yet
        }

        // Get all valid targets
        List<EntityLivingBase> validTargets = getValidTargets();
        
        if (validTargets.isEmpty()) {
            currentTarget = null;
            return;
        }

        // Select target based on mode
        EntityLivingBase newTarget = selectTarget(validTargets);
        
        if (newTarget != currentTarget) {
            currentTarget = newTarget;
            lastSwitchTime = now;
            
            if (logger.isDebugEnabled()) {
                logger.debug("Switched target to: " + getEntityName(newTarget));
            }
        }
    }

    /**
     * Gets all valid targets based on filters.
     */
    private List<EntityLivingBase> getValidTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
        
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            if (entity == mc.thePlayer) continue;
            
            EntityLivingBase living = (EntityLivingBase) entity;
            
            // Basic checks
            if (living.isDead) continue;
            if (ignoreInvisible.getValue() && living.isInvisible()) continue;
            
            // Priority filter
            if (!matchesPriority(living)) continue;
            
            // Team check
            if (ignoreTeam.getValue() && isTeammate(living)) continue;
            
            // Bot check
            if (antiBot.getValue() && living instanceof EntityPlayer && AntiBotMod.isBot(living)) continue;
            
            // FOV check
            if (!isInFOV(living)) continue;
            
            targets.add(living);
        }
        
        return targets;
    }

    /**
     * Selects the best target from the list based on mode.
     */
    private EntityLivingBase selectTarget(List<EntityLivingBase> targets) {
        String currentMode = mode.getMode();
        
        switch (currentMode) {
            case "Closest":
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceSqToEntity(e)))
                    .orElse(null);
                    
            case "Lowest Health":
                return targets.stream()
                    .min(Comparator.comparingDouble(EntityLivingBase::getHealth))
                    .orElse(null);
                    
            case "Most Damage":
                return targets.stream()
                    .max(Comparator.comparingInt(e -> damageTracker.getOrDefault(e.getUniqueID(), 0)))
                    .orElse(null);
                    
            case "Threat Level":
                return targets.stream()
                    .max(Comparator.comparingDouble(this::calculateThreatLevel))
                    .orElse(null);
                    
            case "Team Aware":
                // Prioritize enemies over neutrals
                List<EntityLivingBase> enemies = targets.stream()
                    .filter(e -> !isTeammate(e))
                    .collect(Collectors.toList());
                    
                if (!enemies.isEmpty()) {
                    return enemies.stream()
                        .min(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceSqToEntity(e)))
                        .orElse(null);
                }
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceSqToEntity(e)))
                    .orElse(null);
                    
            default:
                return targets.get(0);
        }
    }

    /**
     * Checks if entity matches priority filter.
     */
    private boolean matchesPriority(EntityLivingBase entity) {
        String currentPriority = priority.getMode();
        
        boolean isPlayer = entity instanceof EntityPlayer;
        boolean isMob = entity instanceof EntityMob;
        boolean isAnimal = entity instanceof EntityAnimal;
        
        switch (currentPriority) {
            case "Players Only":
                return isPlayer;
            case "Players > Mobs":
                return isPlayer || isMob;
            case "Players > Animals":
                return isPlayer || isAnimal;
            case "All":
                return true;
            default:
                return isPlayer;
        }
    }

    /**
     * Checks if entity is a teammate.
     */
    private boolean isTeammate(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return false;
        
        EntityPlayer player = (EntityPlayer) entity;
        
        // Scoreboard team check
        net.minecraft.scoreboard.Team playerTeam = mc.thePlayer.getTeam();
        net.minecraft.scoreboard.Team targetTeam = player.getTeam();
        
        if (playerTeam != null && targetTeam != null && playerTeam == targetTeam) {
            return true;
        }
        
        // Name color check
        String playerName = mc.thePlayer.getDisplayName().getFormattedText();
        String targetName = player.getDisplayName().getFormattedText();
        
        if (playerName.length() > 2 && targetName.length() > 2) {
            String playerColor = playerName.substring(0, 2);
            String targetColor = targetName.substring(0, 2);
            if (playerColor.equals(targetColor) && playerColor.startsWith("§")) {
                return true;
            }
        }
        
        // Armor color check (leather armor)
        ItemStack playerChest = mc.thePlayer.getCurrentArmor(2);
        ItemStack targetChest = player.getCurrentArmor(2);
        
        if (playerChest != null && targetChest != null &&
            playerChest.getItem() instanceof ItemArmor && targetChest.getItem() instanceof ItemArmor) {
            ItemArmor playerArmor = (ItemArmor) playerChest.getItem();
            ItemArmor targetArmor = (ItemArmor) targetChest.getItem();
            
            if (playerArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER &&
                targetArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                int playerColor = playerArmor.getColor(playerChest);
                int targetColor = targetArmor.getColor(targetChest);
                if (playerColor == targetColor) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Checks if entity is within FOV.
     */
    private boolean isInFOV(EntityLivingBase entity) {
        float fovValue = fov.getValue();
        if (fovValue >= 360) return true; // No FOV limit
        
        double dx = entity.posX - mc.thePlayer.posX;
        double dz = entity.posZ - mc.thePlayer.posZ;
        double dy = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(dy, dist) * 180.0 / Math.PI);
        
        float yawDiff = Math.abs(wrapAngle(mc.thePlayer.rotationYaw - yaw));
        float pitchDiff = Math.abs(wrapAngle(mc.thePlayer.rotationPitch - pitch));
        
        return yawDiff <= fovValue / 2.0f && pitchDiff <= fovValue / 2.0f;
    }

    /**
     * Calculates threat level of an entity.
     */
    private double calculateThreatLevel(EntityLivingBase entity) {
        double threat = 0;
        
        // Health (0-20 points)
        threat += entity.getHealth();
        
        // Distance (closer = more threat, 0-10 points)
        double distance = mc.thePlayer.getDistanceToEntity(entity);
        threat += Math.max(0, 10 - distance);
        
        // Armor (0-20 points)
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i = 0; i < 4; i++) {
                ItemStack armor = player.getCurrentArmor(i);
                if (armor != null && armor.getItem() instanceof ItemArmor) {
                    ItemArmor armorItem = (ItemArmor) armor.getItem();
                    threat += armorItem.damageReduceAmount;
                }
            }
        }
        
        // Weapon (0-10 points)
        ItemStack held = entity.getHeldItem();
        if (held != null && held.getItem() instanceof ItemSword) {
            threat += 10;
        }
        
        // Damage dealt to us (0-20 points)
        threat += damageTracker.getOrDefault(entity.getUniqueID(), 0);
        
        return threat;
    }

    /**
     * Tracks damage from entities.
     */
    public void trackDamage(Entity attacker, int damage) {
        if (attacker instanceof EntityLivingBase) {
            UUID uuid = attacker.getUniqueID();
            damageTracker.put(uuid, damageTracker.getOrDefault(uuid, 0) + damage);
        }
    }

    /**
     * Cleans up old damage tracking data.
     */
    private void cleanupDamageTracker() {
        Set<UUID> currentEntities = new HashSet<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                currentEntities.add(entity.getUniqueID());
            }
        }
        damageTracker.keySet().retainAll(currentEntities);
    }

    /**
     * Wraps angle to -180 to 180 range.
     */
    private float wrapAngle(float angle) {
        angle = angle % 360.0f;
        if (angle >= 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    /**
     * Gets entity display name.
     */
    private String getEntityName(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return ((EntityPlayer) entity).getName();
        }
        return entity.getClass().getSimpleName();
    }

    // ==================== PUBLIC API ====================

    /**
     * Gets the current target.
     * PUBLIC API for other modules.
     */
    public static EntityLivingBase getTarget() {
        if (instance != null && instance.isEnabled()) {
            return instance.currentTarget;
        }
        return null;
    }

    /**
     * Gets all valid targets.
     * PUBLIC API for other modules.
     */
    public static List<EntityLivingBase> getTargets() {
        if (instance != null && instance.isEnabled()) {
            return instance.getValidTargets();
        }
        return new ArrayList<>();
    }

    /**
     * Checks if an entity is a valid target.
     * PUBLIC API for other modules.
     */
    public static boolean isValidTarget(EntityLivingBase entity) {
        if (instance != null && instance.isEnabled()) {
            return instance.getValidTargets().contains(entity);
        }
        return false;
    }

    /**
     * Forces a target switch.
     * PUBLIC API for other modules.
     */
    public static void forceSwitch() {
        if (instance != null && instance.isEnabled()) {
            instance.lastSwitchTime = 0;
        }
    }

    @Override
    protected void onDisable() {
        currentTarget = null;
        damageTracker.clear();
    }
}
