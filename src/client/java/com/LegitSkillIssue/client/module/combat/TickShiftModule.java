package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class TickShiftModule extends Module {
    public final NumberSetting maxTicks = new NumberSetting("Max Ticks", 20.0, 10.0, 100.0, 1.0);
    private int ticks = 0;

    public TickShiftModule() {
        super("TickShift", "Speeds up your game by discharging stored ticks.", Category.COMBAT);
        addSetting(maxTicks);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
            if (ticks < maxTicks.getValue()) {
                ticks++;
            }
        } else {
            if (ticks > 0) {
                // Discharge logic would go here, typically modifying the game timer.
                ticks--;
            }
        }
    }
}
