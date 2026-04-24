package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBotModule extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", 100.0, 0.0, 1000.0, 10.0);
    private long lastAttack = 0;

    public TriggerBotModule() {
        super("TriggerBot", "Attacks entities you look at.", Category.COMBAT);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastAttack < delay.getValue()) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hit = (EntityHitResult) mc.crosshairTarget;
            if (hit.getEntity().isAlive()) {
                mc.interactionManager.attackEntity(mc.player, hit.getEntity());
                mc.player.swingHand(Hand.MAIN_HAND);
                lastAttack = System.currentTimeMillis();
            }
        }
    }
}
