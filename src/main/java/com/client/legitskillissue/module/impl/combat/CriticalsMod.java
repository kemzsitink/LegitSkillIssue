package com.client.legitskillissue.module.impl.combat;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.ModeSetting;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * PERFECT CRITICALS: Packet-based critical hits.
 * 
 * Intelligence Cache implementation:
 * - Fools the server into thinking the player has fallen a tiny distance
 *   just before an attack packet (C02) is sent.
 * - This guarantees a 1.5x damage modifier on the ground.
 */
public class CriticalsMod extends Module {

    public final ModeSetting mode = addSetting(new ModeSetting("Mode", "Crit Style", "Packet", "MiniJump", "NoGround"));

    public CriticalsMod() {
        super("Criticals", Category.COMBAT);
    }

    @Override
    public boolean onPacketSend(net.minecraft.network.Packet<?> rawPacket) {
        if (mc.thePlayer == null) return false;

        // Intercept the attack packet
        if (rawPacket instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) rawPacket;
            
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                // Do not crit if we are in water, climbing, or already jumping naturally
                if (mc.thePlayer.isInWater() || mc.thePlayer.isOnLadder() || !mc.thePlayer.onGround) {
                    return false; // Let the packet send normally
                }

                String currentMode = mode.getMode();

                if (currentMode.equalsIgnoreCase("Packet")) {
                    // Send micro-jump packets to simulate falling
                    double x = mc.thePlayer.posX;
                    double y = mc.thePlayer.posY;
                    double z = mc.thePlayer.posZ;

                    // Vanilla magic numbers for a valid critical fall
                    double[] offsets = {0.0625101, 0.0, 0.012511, 0.0};

                    for (double offset : offsets) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                                x, y + offset, z, false));
                    }
                } else if (currentMode.equalsIgnoreCase("MiniJump")) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.25; // Shorter jump
                }
            }
        }
        return false;
    }
}
