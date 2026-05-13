package com.client.legitskillissue.module.impl.render;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ChamsMod extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean registered = false;

    public ChamsMod() { super("Chams", Category.RENDER); }

    @Override
    protected void onEnable() {
        if (!registered) { MinecraftForge.EVENT_BUS.register(this); registered = true; }
    }

    @Override
    protected void onDisable() {
        if (registered) {
            MinecraftForge.EVENT_BUS.unregister(this);
            registered = false;
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderLivingEvent.Pre event) {
        if (!isEnabled() || !(event.entity instanceof EntityPlayer) || event.entity == mc.thePlayer) return;
        
        net.minecraft.client.renderer.GlStateManager.pushMatrix();
        net.minecraft.client.renderer.GlStateManager.disableDepth();
        net.minecraft.client.renderer.GlStateManager.disableTexture2D();
        
        boolean sameTeam = mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(event.entity.getTeam());
        if (sameTeam) GL11.glColor4f(0.2f, 1.0f, 0.2f, 0.6f);
        else          GL11.glColor4f(1.0f, 0.2f, 0.2f, 0.6f);
    }

    @SubscribeEvent
    public void onRenderPost(RenderLivingEvent.Post event) {
        // ALWAYS restore states if we might have changed them (check type and identity)
        if (!(event.entity instanceof EntityPlayer) || event.entity == mc.thePlayer) return;

        net.minecraft.client.renderer.GlStateManager.enableDepth();
        net.minecraft.client.renderer.GlStateManager.enableTexture2D();
        net.minecraft.client.renderer.GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        net.minecraft.client.renderer.GlStateManager.popMatrix();
    }
}
