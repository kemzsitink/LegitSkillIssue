package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.stream.StreamSupport;

public class ESPModule extends Module {
    public ESPModule() {
        super("ESP", "Highlights entities through walls.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .forEach(e -> e.setGlowing(true));
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .forEach(e -> e.setGlowing(false));
    }
}
