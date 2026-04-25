package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import java.util.stream.StreamSupport;

public class TracersModule extends Module {
    public TracersModule() {
        super("Tracers", "Draws lines to nearby entities.", Category.RENDER);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;

        int centerX = mc.getWindow().getScaledWidth() / 2;
        int centerY = mc.getWindow().getScaledHeight() / 2;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .forEach(e -> {
                    double diffX = e.getX() - mc.player.getX();
                    double diffZ = e.getZ() - mc.player.getZ();
                    
                    float angle = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - mc.player.getYaw() - 90;
                    double rad = Math.toRadians(angle);
                    
                    int x = centerX + (int) (Math.cos(rad) * 50);
                    int y = centerY + (int) (Math.sin(rad) * 50);
                    
                    context.fill(x - 1, y - 1, x + 1, y + 1, 0xFFFF0000);
                });
    }
}
