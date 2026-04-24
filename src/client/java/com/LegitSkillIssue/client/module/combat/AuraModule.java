package com.LegitSkillIssue.client.module.combat;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import com.LegitSkillIssue.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public class AuraModule extends Module {
    public final NumberSetting range = new NumberSetting("Range", 3.8, 3.0, 6.0, 0.1);
    public final NumberSetting cps = new NumberSetting("CPS", 10.0, 1.0, 20.0, 1.0);
    private long lastAttack = 0;

    public AuraModule() {
        super("Aura", "Attacks nearby entities automatically.", Category.COMBAT);
        addSetting(range);
        addSetting(cps);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        long delay = (long) (1000.0 / cps.getValue());
        
        if (now - lastAttack < delay) return;

        Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> e != mc.player)
                .filter(Entity::isAlive)
                .filter(e -> mc.player.distanceTo(e) <= range.getValue())
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            lastAttack = now;
        }
    }
}
