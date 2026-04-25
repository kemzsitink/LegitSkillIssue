package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class BlockOutlineModule extends Module {
    public final NumberSetting width = new NumberSetting("Width", 2.0, 1.0, 10.0, 0.5);

    public BlockOutlineModule() {
        super("BlockOutline", "Customizes the block selection outline.", Category.RENDER);
        addSetting(width);
    }
}
