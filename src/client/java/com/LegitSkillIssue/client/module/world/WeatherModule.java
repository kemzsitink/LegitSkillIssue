package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.ModeSetting;

public class WeatherModule extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Clear", "Clear", "Rain", "Thunder");

    public WeatherModule() {
        super("Weather", "Changes client-side weather.", Category.WORLD);
        addSetting(mode);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        
        // This usually requires a Mixin to WorldRenderer.renderWeather
    }
}
