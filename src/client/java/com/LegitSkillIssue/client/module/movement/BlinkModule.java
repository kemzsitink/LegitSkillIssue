package com.LegitSkillIssue.client.module.movement;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.text.Text;

public class BlinkModule extends Module {
    public final NumberSetting maxPackets = new NumberSetting("Max Packets", 100.0, 10.0, 500.0, 10.0);
    private int packetCount = 0;

    public BlinkModule() {
        super("Blink", "Delays your position packets.", Category.MOVEMENT);
        addSetting(maxPackets);
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        
        packetCount++;
        if (packetCount >= maxPackets.getValue()) {
            setEnabled(false);
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("§6[Blink] §fForce disabled to prevent kick."), false);
            }
        }
    }

    @Override
    protected void onDisable() {
        packetCount = 0;
        // The ClientConnectionMixin handles the release/clearing of packets
    }
}
