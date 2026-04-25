package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbsModule extends Module {
    private final List<Vec3d> positions = new ArrayList<>();

    public BreadcrumbsModule() {
        super("Breadcrumbs", "Draws a line behind you.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        
        if (positions.isEmpty() || mc.player.getPos().distanceTo(positions.get(positions.size() - 1)) > 0.5) {
            positions.add(mc.player.getPos());
        }
        
        if (positions.size() > 500) positions.remove(0);
    }

    @Override
    public void onDisable() {
        positions.clear();
    }
}
