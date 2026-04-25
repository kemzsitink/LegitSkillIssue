package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.text.Text;

public class AutoLogModule extends Module {
    public final NumberSetting health = new NumberSetting("Health", 5.0, 1.0, 20.0, 1.0);

    public AutoLogModule() {
        super("AutoLog", "Automatically disconnects on low health.", Category.MISC);
        addSetting(health);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        if (mc.player.getHealth() <= health.getValue()) {
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLog: Low health detected."));
            setEnabled(false);
        }
    }
}
