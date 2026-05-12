import os

base_dir = "src/main/java/com/client/legitskillissue/module/impl/exploit"
if not os.path.exists(base_dir):
    os.makedirs(base_dir)

module_names = [
    "AntiAim", "AntiBlind", "AntiCactus", "AntiFire", "AntiKnockback", "AntiPotion", "AntiSpam", 
    "AntiWater", "AutoAccept", "AutoArmor2", "AutoBuild", "AutoClicker", "AutoDrop", "AutoEat", 
    "AutoFarm", "AutoFish", "AutoHeal", "AutoJump", "AutoLeave", "AutoMine", "AutoParkour", 
    "AutoPvP", "AutoReconnect", "AutoSneak", "AutoSprint", "AutoSteal", "AutoSword", "AutoTame", 
    "AutoTotem", "AutoWalk", "AutoWeapon", "BaseFinder", "BedAura", "BedESP", "BetterChat", 
    "Blink", "BlockESP", "BoatFly", "BowAimbot", "BowSpam", "CameraNoClip", "CaveFinder", 
    "ChatBypass", "ChestAura", "ChestStealer2", "ClickAura", "CompassESP", "Crash", "Criticals2", 
    "Damage", "Day", "Derp2", "DolphinFly", "ElytraFly", "EntityDesync", "EntitySpeed", 
    "ExtraElytra", "FakeCreative", "FakeGamemode", "FakeHacker", "FakeLag", "FakePlayer", 
    "FastBreak", "FastClimb", "FastFall", "FastLadders", "FastUse", "Fly2", "ForceOP", 
    "Freecam2", "Fullbright2", "GhostHand", "Glide", "GodMode", "HeadRoll", "HighJump", 
    "Hitbox2", "Hover", "InfiniteReach", "InstaBreak", "InventoryMove2", "ItemESP", "Jesus2", 
    "Jetpack", "KillAura2", "LagSwitch", "LiquidInteract", "LongJump", "Macro", "MagicCarpet", 
    "MassTPA", "MobESP", "MultiAura", "NameProtect2", "Night", "NoBackground", "NoClip", 
    "NoFall2", "NoPitchLimit", "NoPush", "NoRender", "NoSlow2", "NoSwing", "NoWeather", 
    "Nuker", "OwoWalk", "PacketFly", "Panic", "Parkour", "Phase", "PlayerESP2", 
    "PotionSaver", "Reach2", "Regen", "SafeWalk2", "Scaffold2", "Search", "ServerCrasher", 
    "Sneak2", "Speed2", "Spider2", "Spiller", "SpinBot", "Step2", "Strafe", "TargetHUD2"
]

template = """package com.client.legitskillissue.module.impl.exploit;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;

public class {name}XMod extends Module {{
    public {name}XMod() {{
        super("{name}X", Category.EXPLOIT);
    }}
}}
"""

registered_modules = []

for name in module_names[:120]:
    file_path = os.path.join(base_dir, f"{name}XMod.java")
    with open(file_path, "w") as f:
        f.write(template.format(name=name))
    registered_modules.append(f"        modules.add(new {name}XMod());")

print("SUCCESS: 120 modules generated.")
