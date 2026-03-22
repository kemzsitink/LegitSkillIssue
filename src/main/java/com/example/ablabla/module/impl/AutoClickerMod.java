package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import java.lang.reflect.Method;

public class AutoClickerMod extends Module {
    private long lastClick = 0;
    private double currentCps = 12.0;
    private Method clickMouseMethod;

    public AutoClickerMod() {
        super("AutoClicker");
        try {
            // "clickMouse" in dev environment
            clickMouseMethod = Minecraft.class.getDeclaredMethod("clickMouse");
        } catch (Exception e) {
            try {
                // "func_147116_af" is the SRG name for clickMouse in 1.8.9
                clickMouseMethod = Minecraft.class.getDeclaredMethod("func_147116_af");
            } catch (Exception ex) {
                System.out.println("[Ablabla-Logger] AutoClicker: Could not find clickMouse method!");
            }
        }
        
        if (clickMouseMethod != null) {
            clickMouseMethod.setAccessible(true);
        }
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null || clickMouseMethod == null) return;
        
        // Button 0 is Left Click
        if (Mouse.isButtonDown(0)) {
            long time = System.currentTimeMillis();
            if (time - lastClick >= 1000.0 / currentCps) {
                lastClick = time;
                // Randomize CPS between 10 and 14 to bypass basic anti-cheats
                currentCps = 10.0 + Math.random() * 4.0;
                
                try {
                    // Call Minecraft's internal raw click method
                    clickMouseMethod.invoke(mc);
                } catch (Exception e) {}
            }
        }
    }
}
