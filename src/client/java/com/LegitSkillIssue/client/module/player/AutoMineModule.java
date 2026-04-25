package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.hit.HitResult;

public class AutoMineModule extends Module {
    public AutoMineModule() {
        super("AutoMine", "Automatically mines the targeted block.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.options == null) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            mc.options.attackKey.setPressed(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.attackKey.setPressed(false);
        }
    }
}
