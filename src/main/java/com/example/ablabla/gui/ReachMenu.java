package com.example.ablabla.gui;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import com.example.ablabla.module.impl.AimAssistMod;
import com.example.ablabla.module.impl.ReachMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;

import java.io.IOException;

public class ReachMenu extends GuiScreen {

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int yOffset = -80;

        int id = 1;
        // Dynamically add buttons for each loaded module
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            this.buttonList.add(new GuiButton(id++, centerX - 100, centerY + yOffset, 200, 20, m.getName() + ": " + (m.isEnabled() ? "ON" : "OFF")));
            yOffset += 22;
        }
        
        // AimAssist FOV Controls
        this.buttonList.add(new GuiButton(100, centerX - 100, centerY + yOffset, 95, 20, "Aim FOV -5"));
        this.buttonList.add(new GuiButton(101, centerX + 5, centerY + yOffset, 95, 20, "Aim FOV +5"));
        yOffset += 22;

        // Reach Distance Controls
        this.buttonList.add(new GuiButton(102, centerX - 100, centerY + yOffset, 95, 20, "Reach -0.1"));
        this.buttonList.add(new GuiButton(103, centerX + 5, centerY + yOffset, 95, 20, "Reach +0.1"));
        yOffset += 22;

        // AimLock Toggle
        this.buttonList.add(new GuiButton(104, centerX - 100, centerY + yOffset, 200, 20, "AimLock: " + (AimAssistMod.aimLock ? "ON" : "OFF")));
        
        this.buttonList.add(new GuiButton(99, centerX - 100, centerY + yOffset + 25, 200, 20, "Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 99) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        
        if (button.id == 100) {
            if (AimAssistMod.fov > 10.0f) AimAssistMod.fov -= 5.0f;
            return;
        }
        
        if (button.id == 101) {
            if (AimAssistMod.fov < 360.0f) AimAssistMod.fov += 5.0f;
            return;
        }

        if (button.id == 102) {
            if (ReachMod.reachDistance > 3.0f) ReachMod.reachDistance -= 0.1f;
            return;
        }

        if (button.id == 103) {
            if (ReachMod.reachDistance < 6.0f) ReachMod.reachDistance += 0.1f;
            return;
        }

        if (button.id == 104) {
            AimAssistMod.aimLock = !AimAssistMod.aimLock;
            button.displayString = "AimLock: " + (AimAssistMod.aimLock ? "ON" : "OFF");
            return;
        }

        int id = 1;
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (button.id == id++) {
                m.toggle();
                button.displayString = m.getName() + ": " + (m.isEnabled() ? "ON" : "OFF");
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Ablabla Hack Menu (Pro Architecture)", this.width / 2, this.height / 2 - 110, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Aim FOV: " + AimAssistMod.fov + " | Reach: " + String.format("%.1f", ReachMod.reachDistance), this.width / 2, this.height / 2 - 100, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
