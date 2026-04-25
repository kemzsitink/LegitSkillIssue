package com.LegitSkillIssue.client.module.player;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall", "Prevents fall damage by spoofing ground.", Category.PLAYER);
    }
}
