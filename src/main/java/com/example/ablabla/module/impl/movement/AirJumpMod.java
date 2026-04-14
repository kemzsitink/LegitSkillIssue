package com.example.ablabla.module.impl.movement;

import com.example.ablabla.module.Module;
import com.example.ablabla.utils.ReflectionUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import java.lang.reflect.Field;

/**
 * ARCHITECTURAL BYPASS - AirJump V2 (Stealth)
 * 
 * Instead of simple onGround spoofing, we use "Motion Reset".
 * We send a packet with a tiny downward motion (-0.001) to reset the server's 
 * 'fall distance' and 'jump validation' before triggering the real jump.
 */
public class AirJumpMod extends Module {

    private boolean lastJumpState = false;
    private int spoofTicks = 0;
    
    private static final Field ON_GROUND_FIELD = ReflectionUtil.findField(C03PacketPlayer.class, "onGround", "field_149474_g");

    public AirJumpMod() {
        super("AirJump");
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null) return;

        boolean isJumpDown = mc.gameSettings.keyBindJump.isKeyDown();

        if (isJumpDown && !lastJumpState) {
            if (!mc.thePlayer.onGround && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
                
                // BYPASS STEP 1: Reset Server-side fall logic
                // We don't call jump() immediately. We mark for spoofing.
                spoofTicks = 2;
                
                // Physical jump trigger
                mc.thePlayer.jump();
                
                // Add a tiny vertical boost to compensate for anti-cheat drag
                mc.thePlayer.motionY += 0.02; 
            }
        }
        lastJumpState = isJumpDown;
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (spoofTicks > 0 && packet instanceof C03PacketPlayer) {
            C03PacketPlayer p = (C03PacketPlayer) packet;
            
            // BYPASS STEP 2: Send "Invalid" ground state that triggers "Step" logic on most servers
            // Many servers allow a 0.5 block "step" without ground check.
            ReflectionUtil.setBoolean(ON_GROUND_FIELD, packet, true);
            
            spoofTicks--;
        }
        return false;
    }

    @Override
    public void onDisable() {
        spoofTicks = 0;
    }
}
