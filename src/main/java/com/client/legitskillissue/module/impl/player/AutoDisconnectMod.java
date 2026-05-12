package com.client.legitskillissue.module.impl.player;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import net.minecraft.client.gui.GuiMainMenu;

public class AutoDisconnectMod extends Module {

    public final NumberSetting health = addSetting(new NumberSetting("Health", "Disconnect at health", 1f, 20f, 1f, 6f));

    public AutoDisconnectMod() {
        super("AutoDisconnect", Category.PLAYER);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (mc.thePlayer.getHealth() <= health.getValue()) {
                mc.theWorld.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);
                mc.displayGuiScreen(new GuiMainMenu());
                this.setEnabled(false);
            }
        }
    }
}
