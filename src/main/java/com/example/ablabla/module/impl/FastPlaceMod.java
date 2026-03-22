package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;

public class FastPlaceMod extends Module {
    private Field delayTimerField;

    public FastPlaceMod() {
        super("FastPlace");
        try {
            // Dev workspace name
            delayTimerField = Minecraft.class.getDeclaredField("rightClickDelayTimer");
        } catch (Exception e) {
            try {
                // Obfuscated SRG name for 1.8.9
                delayTimerField = Minecraft.class.getDeclaredField("field_71467_ac");
            } catch (Exception ex) {
                System.out.println("[Ablabla-Logger] FastPlace: Failed to find rightClickDelayTimer field.");
            }
        }
        if (delayTimerField != null) {
            delayTimerField.setAccessible(true);
        }
    }

    @Override
    public void onTick() {
        if (mc.thePlayer == null || delayTimerField == null) return;
        try {
            // Set delay to 0 to place instantly when right clicking
            delayTimerField.set(mc, 0);
        } catch (Exception e) {}
    }

    @Override
    public void onDisable() {
        if (delayTimerField == null) return;
        try {
            // Reset to default Minecraft right click delay (4 ticks)
            delayTimerField.set(mc, 4);
        } catch (Exception e) {}
    }
}
