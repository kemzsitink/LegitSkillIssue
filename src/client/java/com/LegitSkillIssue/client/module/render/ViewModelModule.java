package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;

public class ViewModelModule extends Module {
    public final NumberSetting posX = new NumberSetting("X", 0.0, -2.0, 2.0, 0.1);
    public final NumberSetting posY = new NumberSetting("Y", 0.0, -2.0, 2.0, 0.1);
    public final NumberSetting posZ = new NumberSetting("Z", 0.0, -2.0, 2.0, 0.1);

    public ViewModelModule() {
        super("ViewModel", "Customizes held item position.", Category.RENDER);
        addSetting(posX);
        addSetting(posY);
        addSetting(posZ);
    }
}
