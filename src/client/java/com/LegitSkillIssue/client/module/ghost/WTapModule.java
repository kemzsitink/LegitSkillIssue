package com.LegitSkillIssue.client.module.ghost;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class WTapModule extends Module {
    private int waitTicks = 0;

    public WTapModule() {
        super("WTap", "Resets sprint to increase knockback.", Category.GHOST);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.options == null) return;

        if (mc.options.attackKey.isPressed()) {
            waitTicks = 2;
        }

        if (waitTicks > 0) {
            mc.options.forwardKey.setPressed(false);
            waitTicks--;
        } else if (mc.player.input.movementForward > 0) {
            // Restore if not already handled by MC
            // mc.options.forwardKey.setPressed(true);
        }
    }
}
