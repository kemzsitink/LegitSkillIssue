package com.example.ablabla.gui;

import com.example.ablabla.module.Module;
import com.example.ablabla.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.stream.Collectors;

public class HudRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int COLOR_ACTIVE = 0xFF55FF55; // green
    private static final int COLOR_SHADOW = 0xFF000000;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.thePlayer == null || mc.gameSettings.showDebugInfo) return;

        List<Module> active = ModuleManager.INSTANCE.getModules().stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());

        if (active.isEmpty()) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() - 2;
        int y = 2;

        for (Module m : active) {
            String text = m.getName();
            int textWidth = mc.fontRendererObj.getStringWidth(text);
            // shadow
            mc.fontRendererObj.drawString(text, x - textWidth + 1, y + 1, COLOR_SHADOW);
            // text
            mc.fontRendererObj.drawString(text, x - textWidth, y, COLOR_ACTIVE);
            y += mc.fontRendererObj.FONT_HEIGHT + 1;
        }
    }
}
