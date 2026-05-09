package com.client.legitskillissue.module.impl.movement;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * ParkourMod - Assists with parkour movements.
 * 
 * FEATURES:
 * - Auto Jump: Jump at block edges
 * - Safe Walk: Prevent falling off edges
 * - Jump Boost: Slight jump height increase
 * - Head Hitter: Auto jump into ceiling for boost
 * - Water Bucket: Auto place water when falling
 * 
 * MODES:
 * - Legit: Only auto jump
 * - Semi: Auto jump + safe walk
 * - Full: All features
 */
public class ParkourMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Parkour mode",
        "Legit", "Semi", "Full"));
    public final NumberSetting edgeDistance = addSetting(new NumberSetting("Edge Distance", "Distance from edge to jump", 
        0.1f, 0.5f, 0.05f, 0.3f));
    public final NumberSetting fallThreshold = addSetting(new NumberSetting("Fall Threshold", "Blocks before water bucket", 
        3f, 20f, 1f, 10f));
    public final BooleanSetting autoJump = addSetting(new BooleanSetting("Auto Jump", "Jump at edges", true));
    public final BooleanSetting safeWalk = addSetting(new BooleanSetting("Safe Walk", "Prevent falling", true));
    public final BooleanSetting jumpBoost = addSetting(new BooleanSetting("Jump Boost", "Slight jump boost", false));
    public final BooleanSetting headHitter = addSetting(new BooleanSetting("Head Hitter", "Jump into ceiling", false));
    public final BooleanSetting waterBucket = addSetting(new BooleanSetting("Water Bucket", "Auto water bucket", false));

    private boolean hasJumped = false;
    private int waterSlot = -1;

    public ParkourMod() {
        super("Parkour", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        String currentMode = mode.getMode();
        
        // Auto Jump
        if (shouldUseFeature("autoJump", currentMode) && autoJump.getValue()) {
            handleAutoJump();
        }
        
        // Safe Walk
        if (shouldUseFeature("safeWalk", currentMode) && safeWalk.getValue()) {
            handleSafeWalk();
        }
        
        // Jump Boost
        if (shouldUseFeature("jumpBoost", currentMode) && jumpBoost.getValue()) {
            handleJumpBoost();
        }
        
        // Head Hitter
        if (shouldUseFeature("headHitter", currentMode) && headHitter.getValue()) {
            handleHeadHitter();
        }
        
        // Water Bucket
        if (shouldUseFeature("waterBucket", currentMode) && waterBucket.getValue()) {
            handleWaterBucket();
        }
    }

    /**
     * Auto jump at block edges.
     */
    private void handleAutoJump() {
        if (!mc.thePlayer.onGround || mc.thePlayer.isSneaking()) return;
        if (mc.thePlayer.movementInput.moveForward <= 0) return;
        
        // Check if near edge
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        
        // Get direction player is moving
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double checkX = x + Math.sin(-yaw) * edgeDistance.getValue();
        double checkZ = z + Math.cos(yaw) * edgeDistance.getValue();
        
        BlockPos currentPos = new BlockPos(x, y - 1, z);
        BlockPos nextPos = new BlockPos(checkX, y - 1, checkZ);
        
        Block currentBlock = mc.theWorld.getBlockState(currentPos).getBlock();
        Block nextBlock = mc.theWorld.getBlockState(nextPos).getBlock();
        
        // If standing on block but next block is air, jump
        if (!(currentBlock instanceof BlockAir) && nextBlock instanceof BlockAir && !hasJumped) {
            mc.thePlayer.jump();
            hasJumped = true;
        }
        
        if (!mc.thePlayer.onGround) {
            hasJumped = false;
        }
    }

    /**
     * Prevent falling off edges.
     */
    private void handleSafeWalk() {
        if (!mc.thePlayer.onGround) return;
        
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        
        // Check all directions around player
        boolean safeToMove = true;
        for (double offsetX = -0.3; offsetX <= 0.3; offsetX += 0.3) {
            for (double offsetZ = -0.3; offsetZ <= 0.3; offsetZ += 0.3) {
                BlockPos checkPos = new BlockPos(x + offsetX, y - 1, z + offsetZ);
                Block block = mc.theWorld.getBlockState(checkPos).getBlock();
                if (block instanceof BlockAir) {
                    safeToMove = false;
                    break;
                }
            }
            if (!safeToMove) break;
        }
        
        // Slow down if near edge
        if (!safeToMove && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.motionX *= 0.3;
            mc.thePlayer.motionZ *= 0.3;
        }
    }

    /**
     * Slight jump height boost.
     */
    private void handleJumpBoost() {
        if (mc.thePlayer.motionY > 0 && mc.thePlayer.motionY < 0.42) {
            mc.thePlayer.motionY *= 1.05; // 5% boost
        }
    }

    /**
     * Jump into ceiling for momentum boost.
     */
    private void handleHeadHitter() {
        if (!mc.thePlayer.onGround) return;
        
        BlockPos headPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ);
        Block headBlock = mc.theWorld.getBlockState(headPos).getBlock();
        
        // If there's a block above, jump into it
        if (!(headBlock instanceof BlockAir) && mc.thePlayer.movementInput.moveForward > 0) {
            if (mc.thePlayer.ticksExisted % 10 == 0) { // Every 10 ticks
                mc.thePlayer.jump();
            }
        }
    }

    /**
     * Auto place water bucket when falling.
     */
    private void handleWaterBucket() {
        if (mc.thePlayer.fallDistance < fallThreshold.getValue()) return;
        if (mc.thePlayer.onGround) return;
        
        // Find water bucket
        waterSlot = findWaterBucket();
        if (waterSlot == -1) return;
        
        // Check if about to hit ground
        BlockPos groundPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 3, mc.thePlayer.posZ);
        Block groundBlock = mc.theWorld.getBlockState(groundPos).getBlock();
        
        if (!(groundBlock instanceof BlockAir)) {
            // Switch to water bucket
            int previousSlot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = waterSlot;
            
            // Place water
            mc.playerController.onPlayerRightClick(
                mc.thePlayer, 
                mc.theWorld, 
                mc.thePlayer.inventory.getCurrentItem(), 
                groundPos, 
                EnumFacing.UP, 
                mc.thePlayer.getPositionVector()
            );
            
            // Switch back
            mc.thePlayer.inventory.currentItem = previousSlot;
        }
    }

    /**
     * Finds water bucket in hotbar.
     */
    private int findWaterBucket() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == net.minecraft.init.Items.water_bucket) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if feature should be used based on mode.
     */
    private boolean shouldUseFeature(String feature, String mode) {
        switch (mode) {
            case "Legit":
                return feature.equals("autoJump");
            case "Semi":
                return feature.equals("autoJump") || feature.equals("safeWalk");
            case "Full":
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDisable() {
        hasJumped = false;
        waterSlot = -1;
    }
}
