package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {
    public final NumberSetting size = new NumberSetting("Size", 5.0, 1.0, 20.0, 0.5);
    public final NumberSetting gap = new NumberSetting("Gap", 2.0, 0.0, 10.0, 0.5);
    public final NumberSetting thickness = new NumberSetting("Thickness", 1.0, 0.5, 3.0, 0.1);

    public CrosshairModule() {
        super("Crosshair", "Customizes your crosshair.", Category.RENDER);
        addSetting(size);
        addSetting(gap);
        addSetting(thickness);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        int x = mc.getWindow().getScaledWidth() / 2;
        int y = mc.getWindow().getScaledHeight() / 2;
        int s = size.getValue().intValue();
        int g = gap.getValue().intValue();
        int t = thickness.getValue().intValue();

        // Horizontal Left
        context.fill(x - g - s, y - t, x - g, y + t, 0xFF00FF00);
        // Horizontal Right
        context.fill(x + g, y - t, x + g + s, y + t, 0xFF00FF00);
        // Vertical Top
        context.fill(x - t, y - g - s, x + t, y - g, 0xFF00FF00);
        // Vertical Bottom
        context.fill(x - t, y + g, x + t, y + g + s, 0xFF00FF00);
    }
}
