package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.misc.AutoTPAModule;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onAddMessage(Text message, CallbackInfo ci) {
        AutoTPAModule autoTPA = (AutoTPAModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof AutoTPAModule)
                .findFirst().orElse(null);

        if (autoTPA != null && autoTPA.isEnabled()) {
            String msg = message.getString().toLowerCase();
            if (msg.contains("has requested to teleport") || msg.contains("tpa")) {
                net.minecraft.client.MinecraftClient.getInstance().player.networkHandler.sendCommand("tpaccept");
            }
        }
    }
}
