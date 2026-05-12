package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class NameProtectMod extends Module {

    public NameProtectMod() {
        super("NameProtect", Category.RENDER);
    }
    
    // Requires a mixin in FontRenderer#renderString to replace mc.getSession().getUsername() with "You"
}
