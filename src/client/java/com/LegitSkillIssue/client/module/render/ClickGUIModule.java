package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.gui.ClickGuiScreen;

public class ClickGUIModule extends Module {
    public ClickGUIModule() {
        super("ClickGUI", "Opens the module menu.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.currentScreen == null) {
            mc.setScreen(new ClickGuiScreen());
        }
        setEnabled(false); // Toggle off immediately so it can be re-enabled to open again
    }
}
