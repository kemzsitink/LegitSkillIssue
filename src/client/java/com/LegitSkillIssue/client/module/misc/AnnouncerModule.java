package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.util.math.Vec3d;

public class AnnouncerModule extends Module {
    private Vec3d lastPos;
    private double distance = 0;

    public AnnouncerModule() {
        super("Announcer", "Announces your actions in chat.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (lastPos == null) {
            lastPos = mc.player.getPos();
            return;
        }

        distance += mc.player.getPos().distanceTo(lastPos);
        lastPos = mc.player.getPos();

        if (distance >= 100) {
            mc.player.networkHandler.sendChatMessage("I just walked 100 blocks thanks to LegitSkillIssue!");
            distance = 0;
        }
    }
}
