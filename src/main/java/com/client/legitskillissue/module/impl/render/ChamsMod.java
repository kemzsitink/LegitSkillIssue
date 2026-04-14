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

    @SubscribeEvent
    public void onRenderPre(RenderLivingEvent.Pre event) {
        if (!isEnabled() || !(event.entity instanceof EntityPlayer) || event.entity == mc.thePlayer) return;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        boolean sameTeam = mc.thePlayer.getTeam() != null && mc.thePlayer.getTeam().equals(event.entity.getTeam());
        if (sameTeam) GL11.glColor4f(0.2f, 1.0f, 0.2f, 0.6f);
        else          GL11.glColor4f(1.0f, 0.2f, 0.2f, 0.6f);
    }

    @SubscribeEvent
    public void onRenderPost(RenderLivingEvent.Post event) {
        if (!isEnabled() || !(event.entity instanceof EntityPlayer) || event.entity == mc.thePlayer) return;
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
