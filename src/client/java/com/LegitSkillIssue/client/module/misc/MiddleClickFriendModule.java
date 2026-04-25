package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.text.Text;

public class MiddleClickFriendModule extends Module {
    private boolean pressed = false;

    public MiddleClickFriendModule() {
        super("MiddleClickFriend", "Middle-click players to friend them.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.options.pickItemKey.isPressed()) {
            if (!pressed) {
                if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult hit = (EntityHitResult) mc.crosshairTarget;
                    if (hit.getEntity() instanceof PlayerEntity player) {
                        String name = player.getName().getString();
                        mc.player.sendMessage(Text.literal("§b[Friend] §fToggled friend status for: " + name), false);
                    }
                }
                pressed = true;
            }
        } else {
            pressed = false;
        }
    }
}
