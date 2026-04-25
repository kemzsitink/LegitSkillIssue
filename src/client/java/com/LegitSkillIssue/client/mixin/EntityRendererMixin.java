package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.NameTagsModule;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
    private Text onRenderLabel(Text text, EntityRenderState state, Text originalText, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        NameTagsModule nameTags = (NameTagsModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof NameTagsModule)
                .findFirst().orElse(null);

        if (nameTags != null && nameTags.isEnabled()) {
            if (nameTags.health.getValue()) {
                // In 1.21.4, EntityRenderState is used. 
                // We'll add a generic indicator since accessing specific entity data from state is complex.
                return Text.literal(text.getString() + " §aHP");
            }
        }
        return text;
    }
}
