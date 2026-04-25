package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {
    public final NumberSetting size = new NumberSetting("Size", 5.0, 1.0, 20.0, 0.5);

    public CrosshairModule() {
        super("Crosshair", "Customizes your crosshair.", Category.RENDER);
        addSetting(size);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        int x = mc.getWindow().getScaledWidth() / 2;
        int y = mc.getWindow().getScaledHeight() / 2;
        int s = size.getValue().intValue();

        context.fill(x - s, y - 1, x + s, y + 1, 0xFF00FF00); // Horizontal
        context.fill(x - 1, y - s, x + 1, y + s, 0xFF00FF00); // Vertical
    }
}
