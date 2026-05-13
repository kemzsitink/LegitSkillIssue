package com.legitskillissue.client.gui;

import java.awt.Color;

/**
 * Senior-level UI Constants for the Elementa GUI.
 * Maintains a cohesive visual identity across all components.
 */
public class GuiConstants {
    // Colors - "Glass & Liquid" Palette from Prototype
    public static final Color BG_PANEL = new Color(20, 20, 25, 115); // Prototype rgba(20, 20, 25, 0.45)
    public static final Color BG_HEADER = new Color(0, 0, 0, 100);   // Prototype black/40
    public static final Color BORDER = new Color(255, 255, 255, 20); // Prototype border-panel-border
    
    public static final Color ACCENT = new Color(79, 172, 238);
    public static final Color ACCENT_GLOW = new Color(79, 172, 238, 100);
    public static final Color ACCENT_VIBE = new Color(79, 172, 238, 38); // Module active bg
    
    public static final Color BG_MODULE_IDLE = new Color(0, 0, 0, 0);
    public static final Color BG_MODULE_HOVER = new Color(255, 255, 255, 13);
    
    public static final Color TEXT_MAIN = Color.WHITE;
    public static final Color TEXT_DIM = new Color(160, 160, 165); // #a0a0a5
    
    // Sizing
    public static final float PANEL_WIDTH = 85.0f; // Adjusted for better text fit
    public static final float MODULE_HEIGHT = 13.0f;
    public static final float HEADER_HEIGHT = 14.0f;
    public static final float SETTING_HEIGHT = 11.0f;
    
    // Animation Config
    public static final int ANIM_DURATION = 250;
    public static final int ANIM_DURATION_SLOW = 400;
}
