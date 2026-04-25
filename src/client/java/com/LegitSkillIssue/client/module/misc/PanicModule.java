package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.module.ModuleManager;

public class PanicModule extends Module {
    public PanicModule() {
        super("Panic", "Disables all modules immediately.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m != this && m.isEnabled()) {
                m.setEnabled(false);
            }
        }
        setEnabled(false);
    }
}
