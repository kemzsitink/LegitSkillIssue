package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.stream.StreamSupport;

public class HealthESPModule extends Module {
    public HealthESPModule() {
        super("HealthESP", "Shows health of entities on HUD.", Category.RENDER);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;

        int y = 70;
        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(e -> mc.player.distanceTo(e) <= 15.0)
                .forEach(e -> {
                    LivingEntity living = (LivingEntity) e;
                    String text = living.getName().getString() + " §a" + (int)living.getHealth() + "hp";
                    // context.drawTextWithShadow(mc.textRenderer, text, 10, y, -1); 
                    // (Commented to avoid HUD clutter, will show only near crosshair in real logic)
                });
    }
}
