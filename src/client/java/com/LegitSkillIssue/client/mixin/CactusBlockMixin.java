package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.player.AntiCactusModule;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        AntiCactusModule antiCactus = (AntiCactusModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AntiCactusModule)
                .findFirst().orElse(null);

        if (antiCactus != null && antiCactus.isEnabled() && entity == net.minecraft.client.MinecraftClient.getInstance().player) {
            ci.cancel();
        }
    }
}
