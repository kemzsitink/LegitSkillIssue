package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.ModeSetting;

public class CriticalsModule extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", "Jump", "Jump", "Packet");

    public CriticalsModule() {
        super("Criticals", "Makes all your hits criticals.", Category.COMBAT);
        addSetting(mode);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        
        // Basic Jump Criticals: if we attack while on ground, jump.
        // In a real client, this should be triggered ON attack.
        // For a basic stub, we check if attacking and on ground.
        if (mc.options.attackKey.isPressed() && mc.player.isOnGround()) {
            if (mode.getValue().equals("Jump")) {
                mc.player.jump();
            }
        }
    }
}
