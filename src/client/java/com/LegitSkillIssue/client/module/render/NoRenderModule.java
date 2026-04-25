package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.BooleanSetting;

public class NoRenderModule extends Module {
    public final BooleanSetting weather = new BooleanSetting("Weather", true);

    public NoRenderModule() {
        super("NoRender", "Prevents certain things from rendering.", Category.RENDER);
        addSetting(weather);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;

        if (weather.getValue()) {
            // This only affects client-side perception
            // mc.world.setRainGradient(0); 
            // mc.world.setThunderGradient(0);
        }
    }
}
