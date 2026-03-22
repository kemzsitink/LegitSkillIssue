package com.example.ablabla.module.impl;

import com.example.ablabla.module.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AimAssistMod extends Module {
    
    // FOV ngắm: góc tính từ tâm màn hình
    public static float fov = 45.0f;
    public static boolean aimLock = true;
    
    public AimAssistMod() {
        super("AimAssist");
        // We need to register this to Forge's EVENT_BUS manually for the render event
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null || !Mouse.isButtonDown(0)) return;

        Entity target = null;
        double range = 4.5;
        int targetCount = 0;
        double closestDist = range;

        // Quét tìm mục tiêu
        for (Entity e : mc.theWorld.loadedEntityList) {
            if (isValidTarget(e)) {
                double dist = mc.thePlayer.getDistanceToEntity(e);
                if (dist < range) {
                    targetCount++;
                    // Nếu AimLock đang tắt, ta tìm người gần nhất
                    if (!aimLock && dist < closestDist) {
                        closestDist = dist;
                        target = e;
                    } else if (aimLock) {
                        target = e; // Lưu lại để check targetCount sau
                    }
                }
            }
        }

        // Logic xử lý mục tiêu
        if (aimLock) {
            // Chế độ AimLock: CHỈ hoạt động nếu có DUY NHẤT 1 người
            if (targetCount != 1 || target == null) return;
        } else {
            // Chế độ AimAssist thường: Hoạt động với người gần nhất tìm được
            if (target == null) return;
        }

        // Kiểm tra xem mục tiêu có nằm trong vòng tròn FOV không
        if (!isInFov(target, fov)) return;

        double dx = target.posX - mc.thePlayer.posX;
        double dz = target.posZ - mc.thePlayer.posZ;
        double dy = (target.posY + target.getEyeHeight() * 0.7) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        
        double dist = MathHelper.sqrt_double(dx * dx + dz * dz);
        
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(dy, dist) * 180.0D / Math.PI);
        
        float yawDiff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        float pitchDiff = MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch);
        
        // Tốc độ kéo tâm: AimLock (0.45f) nhanh hơn AimAssist (0.15f)
        float speed = aimLock ? 0.45f : 0.15f; 
        
        mc.thePlayer.rotationYaw += yawDiff * speed;
        mc.thePlayer.rotationPitch += pitchDiff * speed;
    }
    
    private boolean isValidTarget(Entity e) {
        if (!(e instanceof EntityPlayer)) return false; // Chỉ ngắm vào người
        if (e == mc.thePlayer || e.isInvisible()) return false;
        if (mc.thePlayer.isDead || e.isDead) return false; // Không ngắm người chết
        
        return true;
    }
    
    private boolean isInFov(Entity e, float currentFov) {
        double dx = e.posX - mc.thePlayer.posX;
        double dz = e.posZ - mc.thePlayer.posZ;
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float yawDiff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        return Math.abs(yawDiff) <= currentFov / 2.0f; // Nếu góc lệch nằm trong phạm vi FOV
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Chỉ vẽ khi module đang bật, ở góc nhìn thứ nhất và loại Element là CROSSHAIRS (vẽ chung với tâm màn hình)
        if (!isEnabled() || mc.gameSettings.thirdPersonView != 0 || event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int centerX = sr.getScaledWidth() / 2;
        int centerY = sr.getScaledHeight() / 2;
        
        // Tính bán kính vòng tròn dựa trên góc FOV (Công thức gần đúng cho màn hình)
        float radius = (fov / mc.gameSettings.fovSetting) * (sr.getScaledWidth() / 2.0f);

        // Bắt đầu vẽ hình học bằng OpenGL
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D(); // Tắt texture để vẽ màu trơn
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f); // Màu trắng, độ mờ (Alpha) 30%
        GL11.glLineWidth(1.5f);
        
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i += 5) { // Vẽ mỗi 5 độ để tạo thành vòng tròn
            double angle = i * Math.PI / 180.0;
            double x = centerX + Math.sin(angle) * radius;
            double y = centerY + Math.cos(angle) * radius;
            GL11.glVertex2d(x, y);
        }
        GL11.glEnd();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
