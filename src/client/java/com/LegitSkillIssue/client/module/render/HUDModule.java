package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import java.util.List;
import java.util.stream.Collectors;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD", "Shows active modules on screen.", Category.RENDER);
        setEnabled(true); // Default ON
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.options.hudHidden) return;

        int y = 5;
        context.drawTextWithShadow(mc.textRenderer, "§bLegitSkillIssue §f1.21.4", 5, y, -1);
        y += 10;

        List<Module> active = ModuleManager.INSTANCE.getModules().stream()
                .filter(Module::isEnabled)
                .sorted((m1, m2) -> mc.textRenderer.getWidth(m2.getName()) - mc.textRenderer.getWidth(m1.getName()))
                .collect(Collectors.toList());

        for (Module m : active) {
            context.drawTextWithShadow(mc.textRenderer, m.getName(), 5, y, -1);
            y += 10;
        }
    }
}
