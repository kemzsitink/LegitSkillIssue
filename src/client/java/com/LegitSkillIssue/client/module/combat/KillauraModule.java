package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KillauraModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1);
    public final NumberSetting cps = new NumberSetting("CPS", 10.0, 1.0, 20.0, 1.0);
    private long lastAttack = 0;

    public KillauraModule() {
        super("Killaura", "Attacks multiple entities around you.", Category.COMBAT);
        addSetting(range);
        addSetting(cps);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < (1000 / cps.getValue())) return;

        List<Entity> targets = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= range.getValue())
                .collect(Collectors.toList());

        for (Entity target : targets) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        
        if (!targets.isEmpty()) {
            lastAttack = now;
        }
    }
}
