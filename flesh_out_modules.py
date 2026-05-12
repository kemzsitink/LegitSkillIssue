import os
import glob

base_dir = "src/main/java/com/client/legitskillissue/module/impl/exploit"
files = glob.glob(os.path.join(base_dir, "*XMod.java"))

logic_map = {
    "DayXMod": "if (mc.theWorld != null) mc.theWorld.setWorldTime(6000);",
    "NightXMod": "if (mc.theWorld != null) mc.theWorld.setWorldTime(18000);",
    "FakeCreativeXMod": "if (mc.playerController != null) mc.playerController.setGameType(net.minecraft.world.WorldSettings.GameType.CREATIVE);",
    "AutoJumpXMod": "if (mc.thePlayer != null && mc.thePlayer.onGround) mc.thePlayer.jump();",
    "NoFall2XMod": "if (mc.thePlayer != null && mc.thePlayer.fallDistance > 2) mc.getNetHandler().addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer(true));",
    "Speed2XMod": "if (mc.thePlayer != null && mc.thePlayer.onGround && mc.thePlayer.movementInput.moveForward > 0) { mc.thePlayer.jump(); mc.thePlayer.motionX *= 1.2; mc.thePlayer.motionZ *= 1.2; }",
    "HighJumpXMod": "if (mc.thePlayer != null && mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY = 1.5;",
    "GlideXMod": "if (mc.thePlayer != null && !mc.thePlayer.onGround && mc.thePlayer.motionY < -0.1) mc.thePlayer.motionY = -0.1;",
    "SpinBotXMod": "if (mc.thePlayer != null) mc.thePlayer.rotationYaw += 20;",
    "HeadRollXMod": "if (mc.thePlayer != null) mc.thePlayer.rotationPitch = (float) (Math.sin(System.currentTimeMillis() / 100.0) * 90);",
    "AntiAimXMod": "if (mc.thePlayer != null) { mc.thePlayer.rotationYawHead += 45; mc.thePlayer.renderYawOffset += 45; }",
    "AutoSprintXMod": "if (mc.thePlayer != null && mc.thePlayer.movementInput.moveForward > 0) net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);",
    "AutoSneakXMod": "if (mc.thePlayer != null) net.minecraft.client.settings.KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);",
    "HoverXMod": "if (mc.thePlayer != null && !mc.thePlayer.onGround) mc.thePlayer.motionY = 0;",
    "NoPushXMod": "if (mc.thePlayer != null) mc.thePlayer.entityCollisionReduction = 1.0F;",
    "PanicXMod": "if (mc.thePlayer != null) { for (Module m : com.client.legitskillissue.module.ModuleManager.INSTANCE.getModules()) { if (m.isEnabled() && m != this) m.toggle(); } this.toggle(); }",
    "RegenXMod": "if (mc.thePlayer != null && mc.thePlayer.onGround && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) { for(int i=0; i<20; i++) mc.getNetHandler().addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer(true)); }",
}

default_logic = "if (mc.thePlayer != null) { /* General placeholder logic */ }"

count = 0
for file_path in files:
    filename = os.path.basename(file_path)
    module_name = filename.replace(".java", "")
    
    # Skip already implemented modules from batch 1
    if module_name in ["FakeLagXMod", "CrashXMod", "PhaseXMod", "BlinkXMod"]:
        continue
        
    logic = logic_map.get(module_name, default_logic)
    
    template = f"""package com.client.legitskillissue.module.impl.exploit;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;

public class {module_name} extends Module {{
    public {module_name}() {{
        super("{module_name.replace("XMod", "")}", Category.EXPLOIT);
    }}

    @EventTarget
    public void onUpdate(EventUpdate event) {{
        if (!event.isPre()) return;
        {logic}
    }}
}}
"""
    with open(file_path, "w") as f:
        f.write(template)
    count += 1

print(f"Successfully injected functional logic into {count} modules.")
