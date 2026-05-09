package com.client.legitskillissue.utils;

/**
 * Centralized constants to eliminate magic numbers throughout the codebase.
 * All values are documented with their purpose and anti-cheat implications.
 */
public class Constants {

    // ==================== ROTATION CONSTANTS ====================
    
    /**
     * Maximum rotation delta per tick that appears human-like.
     * Values above 30° per tick are flagged by most anti-cheats as "Impossible Rotation".
     */
    public static final float MAX_ROTATION_DELTA_PER_TICK = 30.0F;

    /**
     * Maximum FOV for aim assist to appear legit.
     * Professional players rarely exceed 45° FOV for aim corrections.
     */
    public static final float MAX_LEGIT_FOV = 45.0F;

    // ==================== PACKET TIMING CONSTANTS ====================
    
    /**
     * Server tick rate (ticks per second).
     * Minecraft servers run at 20 TPS, sending more packets triggers "Packet Spam" flags.
     */
    public static final double SERVER_TPS = 20.0;

    /**
     * Maximum packets per second before triggering anti-cheat.
     * Conservative limit: 20 packets/sec (1 per tick).
     */
    public static final double MAX_PACKETS_PER_SECOND = 20.0;

    /**
     * Milliseconds per server tick.
     */
    public static final long MS_PER_TICK = 50L;

    // ==================== COMBAT CONSTANTS ====================
    
    /**
     * Vanilla Minecraft reach distance (blocks).
     */
    public static final double VANILLA_REACH = 3.0;

    /**
     * Maximum "legit" reach distance (blocks).
     * Values above 3.5 are easily detected by anti-cheats.
     */
    public static final double MAX_LEGIT_REACH = 3.5;

    /**
     * Minimum attack delay (ticks) to appear human.
     * Humans cannot consistently click faster than 15 CPS (1.33 ticks).
     */
    public static final int MIN_HUMAN_ATTACK_DELAY_TICKS = 2;

    /**
     * Critical hit Y-offsets for packet mode.
     * These specific values bypass vanilla critical detection.
     */
    public static final double[] CRITICAL_OFFSETS = {
        0.0625101,  // Initial jump offset
        0.0,        // Ground touch
        0.012511,   // Small hop
        0.0         // Final ground
    };

    // ==================== MOVEMENT CONSTANTS ====================
    
    /**
     * Vanilla friction multiplier applied each tick.
     */
    public static final float VANILLA_FRICTION = 0.91F;

    /**
     * Minimum fall distance (blocks) before taking damage.
     */
    public static final float FALL_DAMAGE_THRESHOLD = 3.0F;

    /**
     * NoFall safe fall distance threshold.
     * Send ground packet when falling more than this distance.
     */
    public static final float NOFALL_SAFE_THRESHOLD = 2.5F;

    /**
     * Maximum Y motion clamp for NoFall to avoid detection.
     */
    public static final double NOFALL_MAX_MOTION_Y = -0.2;

    /**
     * Vanilla item use slowdown multiplier.
     */
    public static final float VANILLA_ITEM_USE_SLOWDOWN = 0.2F;

    /**
     * NoSlow movement multiplier (inverse of slowdown).
     * 5.0x compensates for 0.2x slowdown.
     */
    public static final float NOSLOW_MULTIPLIER = 5.0F;

    /**
     * Minimum Y motion threshold for air jump detection.
     */
    public static final double AIR_JUMP_MOTION_THRESHOLD = 0.01;

    // ==================== HITBOX CONSTANTS ====================
    
    /**
     * Vanilla entity hitbox expansion (blocks).
     */
    public static final float VANILLA_HITBOX_EXPANSION = 0.1F;

    /**
     * Maximum "legit" hitbox expansion (blocks).
     * Values above 0.3 are easily detected.
     */
    public static final float MAX_LEGIT_HITBOX_EXPANSION = 0.3F;

    // ==================== TIMING CONSTANTS ====================
    
    /**
     * Average human reaction time (milliseconds).
     * Used for TriggerBot and AutoClicker delays.
     */
    public static final long HUMAN_REACTION_TIME_MS = 225L;

    /**
     * Standard deviation for human reaction time (milliseconds).
     */
    public static final long HUMAN_REACTION_STDDEV_MS = 50L;

    /**
     * Minimum human reaction time (milliseconds).
     * Reactions faster than 150ms are rare and suspicious.
     */
    public static final long MIN_HUMAN_REACTION_MS = 150L;

    /**
     * Maximum human reaction time (milliseconds).
     * Reactions slower than 400ms appear AFK.
     */
    public static final long MAX_HUMAN_REACTION_MS = 400L;

    /**
     * Maximum human CPS (clicks per second).
     * Professional players rarely exceed 15 CPS consistently.
     */
    public static final double MAX_HUMAN_CPS = 15.0;

    /**
     * BlockHit delay range (milliseconds).
     * Delay between attack and block to appear human.
     */
    public static final int BLOCKHIT_MIN_DELAY_MS = 5;
    public static final int BLOCKHIT_MAX_DELAY_MS = 15;

    // ==================== BACKTRACK CONSTANTS ====================
    
    /**
     * Maximum backtrack delay (milliseconds) before detection.
     * Most anti-cheats flag delays above 500ms.
     */
    public static final int MAX_BACKTRACK_DELAY_MS = 500;

    /**
     * Maximum backtrack queue size per entity.
     * Prevents memory leaks from entity ID reuse.
     */
    public static final int MAX_BACKTRACK_QUEUE_SIZE = 20;

    // ==================== VELOCITY CONSTANTS ====================
    
    /**
     * Minimum velocity reduction percentage to avoid detection.
     * 0% reduction is obvious, 100% reduction is flagged.
     */
    public static final int MIN_VELOCITY_REDUCTION_PERCENT = 10;

    /**
     * Maximum velocity reduction percentage to avoid detection.
     */
    public static final int MAX_VELOCITY_REDUCTION_PERCENT = 90;

    // ==================== EAGLE CONSTANTS ====================
    
    /**
     * Maximum blocks to check below player for Eagle mode.
     */
    public static final int EAGLE_MAX_BLOCKS_BELOW = 3;

    /**
     * Edge distance threshold (blocks) for Eagle activation.
     */
    public static final double EAGLE_EDGE_THRESHOLD = 0.3;

    private Constants() {
        // Prevent instantiation
    }
}
