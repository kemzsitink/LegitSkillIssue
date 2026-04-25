package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.NameTagsModule;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
    private Text onRenderLabel(Text text, Entity entity) {
        NameTagsModule nameTags = (NameTagsModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof NameTagsModule)
                .findFirst().orElse(null);

        if (nameTags != null && nameTags.isEnabled() && entity instanceof LivingEntity living) {
            if (nameTags.health.getValue()) {
                return Text.literal(text.getString() + " §a" + (int) living.getHealth());
            }
        }
        return text;
    }
}
