package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.combat.ReachModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private net.minecraft.util.math.Vec3d onUpdateTargetedEntity(net.minecraft.entity.Entity entity, float tickDelta) {
        ReachModule reach = (ReachModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof ReachModule)
                .findFirst().orElse(null);
        
        return entity.getRotationVec(tickDelta);
    }
}
