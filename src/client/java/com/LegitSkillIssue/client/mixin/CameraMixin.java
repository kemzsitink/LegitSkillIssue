package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.CameraClipModule;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        CameraClipModule cameraClip = (CameraClipModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof CameraClipModule)
                .findFirst().orElse(null);

        if (cameraClip != null && cameraClip.isEnabled()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }
}
