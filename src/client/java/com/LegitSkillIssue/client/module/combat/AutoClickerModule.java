package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import com.LegitSkillIssue.client.setting.BooleanSetting;
import net.minecraft.util.Hand;

public class AutoClickerModule extends Module {
    public final NumberSetting cps = new NumberSetting("CPS", 10.0, 1.0, 20.0, 1.0);
    public final BooleanSetting leftClick = new BooleanSetting("Left Click", true);
    public final BooleanSetting rightClick = new BooleanSetting("Right Click", false);
    private long lastClick = 0;

    public AutoClickerModule() {
        super("AutoClicker", "Automatically clicks for you.", Category.COMBAT);
        addSetting(cps);
        addSetting(leftClick);
        addSetting(rightClick);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        long now = System.currentTimeMillis();
        long delay = (long) (1000.0 / cps.getValue());
        
        if (now - lastClick < delay) return;

        if (leftClick.getValue() && mc.options.attackKey.isPressed()) {
            mc.player.swingHand(Hand.MAIN_HAND);
            if (mc.crosshairTarget != null && mc.interactionManager != null) {
                // In a real client, we might call interactionManager.attackEntity if over an entity
                // but for a basic auto-clicker, swinging and let MC handle it is safer for stubs.
            }
            lastClick = now;
        }
        
        if (rightClick.getValue() && mc.options.useKey.isPressed()) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            lastClick = now;
        }
    }
}
