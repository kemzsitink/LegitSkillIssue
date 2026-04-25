package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.ModuleManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline, net.minecraft.client.render.Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        MatrixStack matrices = new MatrixStack();
        float tickDelta = tickCounter.getTickDelta(false);

        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.isEnabled()) {
                m.onRender3D(matrices, tickDelta);
            }
        }
    }
}
