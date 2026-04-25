package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;

public class SelfDestructModule extends Module {
    public SelfDestructModule() {
        super("SelfDestruct", "Unloads the mod from memory.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            m.setEnabled(false);
        }
        mc.setScreen(null);
        // Real self-destruct would unregister events and mixins (very hard)
        setEnabled(false);
    }
}
