package com.client.legitskillissue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * REFACTORED (God-Tier): Shader Engine Utility.
 * Manages compilation and usage of GLSL shaders for high-end UI rendering.
 */
public class ShaderUtil {

    private final int programId;

    public ShaderUtil(String fragmentShaderName) {
        int program = GL20.glCreateProgram();
        try {
            int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            String source = new BufferedReader(new InputStreamReader(
                    Minecraft.getMinecraft().getResourceManager()
                            .getResource(new net.minecraft.util.ResourceLocation("legitskillissue/shaders/" + fragmentShaderName))
                            .getInputStream())).lines().collect(Collectors.joining("\n"));
            
            GL20.glShaderSource(fragmentShader, source);
            GL20.glCompileShader(fragmentShader);
            
            if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println("Shader Error: " + GL20.glGetShaderInfoLog(fragmentShader, 1024));
            }
            
            GL20.glAttachShader(program, fragmentShader);
            GL20.glLinkProgram(program);
            GL20.glValidateProgram(program);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.programId = program;
    }

    public void start() {
        GL20.glUseProgram(programId);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void setUniform(String name, float... args) {
        int loc = GL20.glGetUniformLocation(programId, name);
        if (args.length == 1) GL20.glUniform1f(loc, args[0]);
        else if (args.length == 2) GL20.glUniform2f(loc, args[0], args[1]);
        else if (args.length == 3) GL20.glUniform3f(loc, args[0], args[1], args[2]);
        else if (args.length == 4) GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
    }

    /**
     * Draws a perfect rounded rectangle using the shader.
     */
    private static final ShaderUtil ROUNDED_RECT_SHADER = new ShaderUtil("rounded_rect.frag");

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, java.awt.Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ROUNDED_RECT_SHADER.start();
        
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float scale = sr.getScaleFactor();
        
        // Pass coordinates to shader in GL space
        ROUNDED_RECT_SHADER.setUniform("location", x * scale, (Minecraft.getMinecraft().displayHeight - (y + height) * scale));
        ROUNDED_RECT_SHADER.setUniform("rectSize", width * scale, height * scale);
        ROUNDED_RECT_SHADER.setUniform("radius", radius * scale);
        ROUNDED_RECT_SHADER.setUniform("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        
        // Draw dummy rect to trigger shader
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
        
        ROUNDED_RECT_SHADER.stop();
        GlStateManager.disableBlend();
    }
}
