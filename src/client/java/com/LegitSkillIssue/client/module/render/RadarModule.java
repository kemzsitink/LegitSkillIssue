package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.stream.StreamSupport;

public class RadarModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 50.0, 10.0, 100.0, 5.0);

    public RadarModule() {
        super("Radar", "Shows nearby entities on a radar.", Category.RENDER);
        addSetting(range);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;

        int size = 100;
        int x = mc.getWindow().getScaledWidth() - size - 5;
        int y = 5;
        
        context.fill(x, y, x + size, y + size, 0x80000000); // BG

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .forEach(e -> {
                    double diffX = e.getX() - mc.player.getX();
                    double diffZ = e.getZ() - mc.player.getZ();
                    
                    double scale = (double)size / (range.getValue() * 2);
                    int dx = (int)(size / 2 + diffX * scale);
                    int dy = (int)(size / 2 + diffZ * scale);
                    
                    if (dx >= 0 && dx < size && dy >= 0 && dy < size) {
                        context.fill(x + dx - 1, y + dy - 1, x + dx + 1, y + dy + 1, 0xFFFF0000);
                    }
                });
        
        // Player dot
        context.fill(x + size / 2 - 1, y + size / 2 - 1, x + size / 2 + 1, y + size / 2 + 1, 0xFFFFFFFF);
    }
}
