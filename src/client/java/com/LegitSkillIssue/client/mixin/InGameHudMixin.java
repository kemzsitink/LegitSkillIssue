package com.LegitSkillIssue.client.mixin;

import com.LegitSkillIssue.client.module.ModuleManager;
import com.LegitSkillIssue.client.module.render.HUDModule;
import com.LegitSkillIssue.client.module.misc.AntiSpamModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private String lastMessage = "";

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        HUDModule hud = (HUDModule) ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m instanceof HUDModule)
                .findFirst().orElse(null);

        if (hud != null && hud.isEnabled()) {
            hud.onRender(context, tickCounter.getTickDelta(false));
        }
    }

    // This is a placeholder for AntiSpam. 
    // addMessage has multiple signatures, we would need to target the right one.
}
