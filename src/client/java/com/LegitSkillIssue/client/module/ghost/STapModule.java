package com.LegitSkillIssue.client.module.ghost;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class STapModule extends Module {
    private int waitTicks = 0;

    public STapModule() {
        super("STap", "Taps S after attacking.", Category.GHOST);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.options == null) return;

        if (mc.options.attackKey.isPressed()) {
            waitTicks = 2;
        }

        if (waitTicks > 0) {
            mc.options.backKey.setPressed(true);
            waitTicks--;
        } else if (waitTicks == 0) {
            // mc.options.backKey.setPressed(false);
        }
    }
}
