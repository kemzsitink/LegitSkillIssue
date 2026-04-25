package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.player.FastBreakModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private float blockBreakProgress;

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void onUpdateBlockBreakingProgress(net.minecraft.util.math.BlockPos pos, net.minecraft.util.math.Direction direction, CallbackInfoReturnable<Boolean> cir) {
        FastBreakModule fastBreak = (FastBreakModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof FastBreakModule)
                .findFirst().orElse(null);

        if (fastBreak != null && fastBreak.isEnabled()) {
            if (blockBreakProgress >= 1.0f) return;
            // Additional progress logic would go here
        }
    }
}
