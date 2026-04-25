package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import java.util.stream.StreamSupport;

public class ItemESPModule extends Module {
    public ItemESPModule() {
        super("ItemESP", "Highlights dropped items.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof ItemEntity)
                .forEach(e -> e.setGlowing(true));
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof ItemEntity)
                .forEach(e -> e.setGlowing(false));
    }
}
