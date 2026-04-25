package com.LegitSkillIssue.client.module.ghost;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class LegitAuraModule extends Module {
    public final NumberSetting cps = new NumberSetting("CPS", 8.0, 1.0, 15.0, 1.0);
    private long lastAttack = 0;

    public LegitAuraModule() {
        super("LegitAura", "Attacks only when looking at targets.", Category.GHOST);
        addSetting(cps);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastAttack < (1000 / cps.getValue())) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hit = (EntityHitResult) mc.crosshairTarget;
            mc.interactionManager.attackEntity(mc.player, hit.getEntity());
            mc.player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }
}
