package com.LegitSkillIssue.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class LegitSkillIssueMixin {
	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		// This code is injected into the start of MinecraftServer.tick()
	}
}
