package com.LegitSkillIssue.client.module.render;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import java.util.HashMap;
import java.util.Map;

public class LogoutSpotsModule extends Module {
    private final Map<String, Vec3d> spots = new HashMap<>();

    public LogoutSpotsModule() {
        super("LogoutSpots", "Highlights where players logged out.", Category.RENDER);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (spots.isEmpty()) return;
        
        int y = 100;
        for (Map.Entry<String, Vec3d> entry : spots.entrySet()) {
            context.drawTextWithShadow(mc.textRenderer, "Logout: " + entry.getKey(), 10, y, 0xFFFF0000);
            y += 10;
        }
    }
}
