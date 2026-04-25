package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class TargetHUDModule extends Module {
    public TargetHUDModule() {
        super("TargetHUD", "Shows target info on screen.", Category.RENDER);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= 7.0)
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target instanceof LivingEntity living) {
            int x = mc.getWindow().getScaledWidth() / 2 + 10;
            int y = mc.getWindow().getScaledHeight() / 2 + 10;
            
            context.fill(x, y, x + 100, y + 35, 0x80000000);
            context.drawTextWithShadow(mc.textRenderer, living.getName().getString(), x + 5, y + 5, -1);
            context.drawTextWithShadow(mc.textRenderer, "Health: " + (int) living.getHealth(), x + 5, y + 15, 0xFFFF0000);
        }
    }
}
