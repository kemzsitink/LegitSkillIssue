package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class WaypointsModule extends Module {
    private BlockPos spawn = new BlockPos(0, 64, 0);

    public WaypointsModule() {
        super("Waypoints", "Shows markers at coordinates.", Category.RENDER);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;

        double dist = Math.sqrt(mc.player.getBlockPos().getSquaredDistance(spawn));
        String text = "Spawn: " + (int)dist + "m";
        
        context.drawTextWithShadow(mc.textRenderer, text, 10, mc.getWindow().getScaledHeight() - 20, 0xFFFFFF00);
    }
}
