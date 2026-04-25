package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import java.util.stream.StreamSupport;

public class FakeHackerModule extends Module {
    public FakeHackerModule() {
        super("FakeHacker", "Makes others look like they are hacking.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;

        StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof PlayerEntity)
                .filter(e -> e != mc.player)
                .forEach(e -> ((PlayerEntity)e).swingHand(Hand.MAIN_HAND));
    }
}
