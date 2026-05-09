package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.utils.Logger;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AntiBot - Detects and filters fake players (bots) used by anti-cheat systems.
 * 
 * DETECTION METHODS:
 * 1. Tab List - Bots not in tab list
 * 2. Name Pattern - Bot names follow patterns (e.g., "Player123", "Bot_456")
 * 3. Spawn Pattern - Multiple entities spawn at same time/location
 * 4. Entity ID - Sequential entity IDs indicate bot spawning
 * 5. Ping - Bots have 0 ping or identical ping
 * 6. Movement - Bots move in identical patterns
 * 7. Invisible - Some bots are invisible
 * 
 * ANTI-CHEAT IMPORTANCE:
 * - Hypixel uses "Watchdog Bots" to detect cheaters
 * - Attacking bots = instant flag
 * - This module is CRITICAL for safety
 */
public class AntiBotMod extends Module {

    public final ModeSetting sensitivity = addSetting(new ModeSetting("Sensitivity", "Detection sensitivity", 
        "Low", "Medium", "High"));
    public final BooleanSetting tabList = addSetting(new BooleanSetting("Tab List", "Check if in tab list", true));
    public final BooleanSetting namePattern = addSetting(new BooleanSetting("Name Pattern", "Detect bot name patterns", true));
    public final BooleanSetting spawnPattern = addSetting(new BooleanSetting("Spawn Pattern", "Detect spawn patterns", true));
    public final BooleanSetting entityId = addSetting(new BooleanSetting("Entity ID", "Check sequential entity IDs", true));
    public final BooleanSetting ping = addSetting(new BooleanSetting("Ping", "Check ping values", true));
    public final BooleanSetting invisible = addSetting(new BooleanSetting("Invisible", "Detect invisible entities", true));
    public final BooleanSetting logDetections = addSetting(new BooleanSetting("Log", "Log bot detections", false));

    private static final Logger logger = Logger.getLogger(AntiBotMod.class);
    
    // Bot detection data
    private final Set<UUID> detectedBots = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> spawnTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> entityIds = new ConcurrentHashMap<>();
    private final Set<UUID> whitelist = ConcurrentHashMap.newKeySet();
    
    // Spawn pattern detection
    private long lastSpawnTime = 0;
    private int spawnBurst = 0;
    
    // Name patterns (common bot patterns)
    private static final String[] BOT_NAME_PATTERNS = {
        "^[a-zA-Z]+[0-9]{3,}$",  // Letters followed by 3+ numbers (e.g., "Player123")
        "^Bot[_-]?[0-9]+$",       // "Bot_123", "Bot-456"
        "^[0-9]+$",               // Only numbers
        "^[a-zA-Z]{1,3}[0-9]+$",  // 1-3 letters + numbers (e.g., "AB123")
        "^NPC[_-]?",              // NPC prefix
        "^Test[_-]?",             // Test prefix
        "^Fake[_-]?",             // Fake prefix
    };

    public AntiBotMod() {
        super("AntiBot", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Check all players in world
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity == mc.thePlayer) continue;
            
            EntityPlayer player = (EntityPlayer) entity;
            UUID uuid = player.getUniqueID();
            
            // Skip whitelisted players
            if (whitelist.contains(uuid)) continue;
            
            // Skip already detected bots
            if (detectedBots.contains(uuid)) continue;
            
            // Run detection checks
            int suspicionScore = 0;
            int maxScore = 0;
            
            if (tabList.getValue()) {
                maxScore += 3;
                if (!isInTabList(player)) {
                    suspicionScore += 3;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Tab List): " + player.getName());
                    }
                }
            }
            
            if (namePattern.getValue()) {
                maxScore += 2;
                if (hasBotnamePattern(player.getName())) {
                    suspicionScore += 2;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Name Pattern): " + player.getName());
                    }
                }
            }
            
            if (spawnPattern.getValue()) {
                maxScore += 2;
                if (isSpawnBurst(player)) {
                    suspicionScore += 2;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Spawn Pattern): " + player.getName());
                    }
                }
            }
            
            if (entityId.getValue()) {
                maxScore += 1;
                if (hasSequentialEntityId(player)) {
                    suspicionScore += 1;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Entity ID): " + player.getName());
                    }
                }
            }
            
            if (ping.getValue()) {
                maxScore += 2;
                if (hasSuspiciousPing(player)) {
                    suspicionScore += 2;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Ping): " + player.getName());
                    }
                }
            }
            
            if (invisible.getValue()) {
                maxScore += 1;
                if (player.isInvisible()) {
                    suspicionScore += 1;
                    if (logDetections.getValue()) {
                        logger.info("Bot detected (Invisible): " + player.getName());
                    }
                }
            }
            
            // Calculate threshold based on sensitivity
            float threshold = 0.5f; // Medium
            if (sensitivity.getMode().equals("Low")) {
                threshold = 0.7f; // Need 70% suspicion
            } else if (sensitivity.getMode().equals("High")) {
                threshold = 0.3f; // Need 30% suspicion
            }
            
            // Mark as bot if suspicion exceeds threshold
            if (maxScore > 0 && (float) suspicionScore / maxScore >= threshold) {
                detectedBots.add(uuid);
                if (logDetections.getValue()) {
                    logger.warn("CONFIRMED BOT: " + player.getName() + " (Score: " + suspicionScore + "/" + maxScore + ")");
                }
            }
        }
        
        // Cleanup old data
        cleanupOldData();
    }

    /**
     * Checks if player is in tab list.
     */
    private boolean isInTabList(EntityPlayer player) {
        if (mc.getNetHandler() == null) return true; // Assume real if can't check
        
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
        return info != null;
    }

    /**
     * Checks if name matches bot patterns.
     */
    private boolean hasBotnamePattern(String name) {
        for (String pattern : BOT_NAME_PATTERNS) {
            if (name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detects spawn bursts (multiple entities spawning at once).
     */
    private boolean isSpawnBurst(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        long now = System.currentTimeMillis();
        
        if (!spawnTimes.containsKey(uuid)) {
            spawnTimes.put(uuid, now);
            
            // Check if this is part of a burst
            if (now - lastSpawnTime < 1000) { // Within 1 second
                spawnBurst++;
                if (spawnBurst >= 3) { // 3+ spawns in 1 second = suspicious
                    return true;
                }
            } else {
                spawnBurst = 1;
            }
            
            lastSpawnTime = now;
        }
        
        return false;
    }

    /**
     * Checks for sequential entity IDs (bots often spawn with sequential IDs).
     */
    private boolean hasSequentialEntityId(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        int entityId = player.getEntityId();
        
        if (!entityIds.containsKey(uuid)) {
            entityIds.put(uuid, entityId);
            
            // Check if ID is sequential with recent entities
            for (int recentId : entityIds.values()) {
                if (Math.abs(entityId - recentId) <= 5 && entityId != recentId) {
                    return true; // IDs within 5 of each other
                }
            }
        }
        
        return false;
    }

    /**
     * Checks for suspicious ping values.
     */
    private boolean hasSuspiciousPing(EntityPlayer player) {
        if (mc.getNetHandler() == null) return false;
        
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
        if (info == null) return true; // Not in tab list = suspicious
        
        int ping = info.getResponseTime();
        
        // Ping = 0 is suspicious (bots often have 0 ping)
        if (ping == 0) return true;
        
        // Check if multiple players have identical ping (bot pattern)
        int identicalPingCount = 0;
        for (NetworkPlayerInfo otherInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (otherInfo.getResponseTime() == ping) {
                identicalPingCount++;
            }
        }
        
        // 3+ players with identical ping = suspicious
        return identicalPingCount >= 3;
    }

    /**
     * Cleans up old tracking data.
     */
    private void cleanupOldData() {
        long now = System.currentTimeMillis();
        
        // Remove spawn times older than 10 seconds
        spawnTimes.entrySet().removeIf(entry -> now - entry.getValue() > 10000);
        
        // Remove entity IDs for players no longer in world
        Set<UUID> currentPlayers = new HashSet<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                currentPlayers.add(entity.getUniqueID());
            }
        }
        entityIds.keySet().retainAll(currentPlayers);
        detectedBots.retainAll(currentPlayers);
    }

    /**
     * Checks if an entity is a bot.
     * PUBLIC API for other modules to use.
     */
    public static boolean isBot(Entity entity) {
        if (!(entity instanceof EntityPlayer)) return false;
        
        // Find AntiBot module
        for (Module module : com.client.legitskillissue.module.ModuleManager.INSTANCE.getModules()) {
            if (module instanceof AntiBotMod && module.isEnabled()) {
                AntiBotMod antiBot = (AntiBotMod) module;
                return antiBot.detectedBots.contains(entity.getUniqueID());
            }
        }
        
        return false;
    }

    /**
     * Adds a player to whitelist (never mark as bot).
     */
    public void addToWhitelist(UUID uuid) {
        whitelist.add(uuid);
        detectedBots.remove(uuid);
    }

    /**
     * Removes a player from whitelist.
     */
    public void removeFromWhitelist(UUID uuid) {
        whitelist.remove(uuid);
    }

    /**
     * Gets the number of detected bots.
     */
    public int getBotCount() {
        return detectedBots.size();
    }

    @Override
    protected void onDisable() {
        // Don't clear detections on disable - keep the data
    }

    @Override
    protected void onEnable() {
        // Clear old detections on enable
        detectedBots.clear();
        spawnTimes.clear();
        entityIds.clear();
        spawnBurst = 0;
        lastSpawnTime = 0;
    }
}
