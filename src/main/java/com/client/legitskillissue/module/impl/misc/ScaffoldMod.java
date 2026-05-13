package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventPacket;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class ScaffoldMod extends Module {

    public final BooleanSetting safeWalk = addSetting(new BooleanSetting("SafeWalk", "Stop at edges", true));
    public final BooleanSetting autoSwing = addSetting(new BooleanSetting("Swing", "Swing hand when placing", true));
    public final NumberSetting delay = addSetting(new NumberSetting("Delay", "Placement delay (ticks)", 0f, 10f, 1f, 0f));
    
    private BlockData currentBlockData;
    private float currentYaw, currentPitch;
    private int delayTimer;
    private boolean isSpoofing;
    private boolean sending;

    public ScaffoldMod() {
        super("Scaffold", Category.MISC);
    }

    @Override
    protected void onEnable() {
        currentBlockData = null;
        delayTimer = 0;
        isSpoofing = false;
        sending = false;
        if (mc.thePlayer != null) {
            currentYaw = mc.thePlayer.rotationYaw;
            currentPitch = mc.thePlayer.rotationPitch;
        }
    }

    @Override
    protected void onDisable() {
        sending = false;
        isSpoofing = false;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (event.isPre()) {
            currentBlockData = getBlockData(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ));
            
            if (currentBlockData != null) {
                // Calculate rotations to the block
                float[] rots = RotationUtils.getRotations(mc.thePlayer.getPositionEyes(1.0f), currentBlockData.hitVec);
                currentYaw = rots[0];
                currentPitch = 82.0f; // Look slightly down
                isSpoofing = true;
            } else {
                isSpoofing = false;
            }

            // SafeWalk logic (if shifting/sneaking wasn't already handled by a mixin, we can just sneak)
            if (safeWalk.getValue() && mc.thePlayer.onGround) {
                // A true safewalk requires a hook in Entity.moveEntity. 
                // We'll simulate by sneaking if near an edge.
                double x = mc.thePlayer.posX;
                double y = mc.thePlayer.posY - 1.0D;
                double z = mc.thePlayer.posZ;
                if (mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockAir) {
                    net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                } else {
                    net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
                }
            }

        } else {
            // Post update - do placement
            if (currentBlockData != null) {
                delayTimer++;
                if (delayTimer >= delay.getInt()) {
                    int blockSlot = getBlockSlot();
                    if (blockSlot != -1) {
                        int prevSlot = mc.thePlayer.inventory.currentItem;
                        
                        // Switch to block
                        if (mc.thePlayer.inventory.currentItem != blockSlot) {
                            mc.thePlayer.inventory.currentItem = blockSlot;
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot));
                        }

                        // Place
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), 
                            currentBlockData.pos, currentBlockData.facing, currentBlockData.hitVec)) {
                            if (autoSwing.getValue()) {
                                mc.thePlayer.swingItem();
                            } else {
                                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            }
                        }

                        // Switch back
                        if (mc.thePlayer.inventory.currentItem != prevSlot) {
                            mc.thePlayer.inventory.currentItem = prevSlot;
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(prevSlot));
                        }
                    }
                    delayTimer = 0;
                }
            }
        }
    }

    @EventTarget
    public void onPacketEvent(EventPacket event) {
        if (sending || !event.isSend || !isSpoofing) return;
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
            event.setCancelled(true);
            sending = true;
            if (packet.isMoving()) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                    packet.getPositionX(), packet.getPositionY(), packet.getPositionZ(), 
                    currentYaw, currentPitch, packet.isOnGround()
                ));
            } else {
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(
                    currentYaw, currentPitch, packet.isOnGround()
                ));
            }
            sending = false;
        }
    }

    private int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block.isFullBlock() && block != Blocks.sand && block != Blocks.gravel) {
                    return i;
                }
            }
        }
        return -1;
    }

    private BlockData getBlockData(BlockPos pos) {
        if (!isPosSolid(pos.add(0, -1, 0))) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
                BlockPos offset = pos.offset(facing);
                if (isPosSolid(offset)) {
                    return new BlockData(offset, facing.getOpposite());
                }
            }
            
            // Check 1 block below and adjacent
            BlockPos posBelow = pos.add(0, -1, 0);
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
                BlockPos offset = posBelow.offset(facing);
                if (isPosSolid(offset)) {
                    return new BlockData(offset, facing.getOpposite());
                }
            }
        } else {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        return null;
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return !(block instanceof BlockAir || block instanceof BlockLiquid || block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.waterlily);
    }

    private static class BlockData {
        public BlockPos pos;
        public EnumFacing facing;
        public Vec3 hitVec;

        public BlockData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
            this.hitVec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).add(new Vec3(facing.getDirectionVec()).normalize().crossProduct(new Vec3(0.5, 0.5, 0.5))); // Approximation
        }
    }
}
