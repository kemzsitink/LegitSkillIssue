import os
import random

java_src_dir = "src/client/java/com/LegitSkillIssue/client/module"
os.makedirs(java_src_dir, exist_ok=True)

categories = ["COMBAT", "MOVEMENT", "RENDER", "PLAYER", "WORLD", "EXPLOIT", "GHOST", "MISC"]

# write Category.java
with open(os.path.join(java_src_dir, "Category.java"), "w") as f:
    f.write("""package com.LegitSkillIssue.client.module;

public enum Category {
    """ + ", ".join(categories) + """
}
""")

# write Module.java
with open(os.path.join(java_src_dir, "Module.java"), "w") as f:
    f.write("""package com.LegitSkillIssue.client.module;

public class Module {
    private String name;
    private String description;
    private Category category;
    private boolean enabled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) onEnable(); else onDisable();
    }
    
    public void toggle() { setEnabled(!enabled); }
    protected void onEnable() {}
    protected void onDisable() {}
}
""")

module_names = {
    "COMBAT": ["Aura", "AutoClicker", "Velocity", "Reach", "Criticals", "BowAimbot", "AutoArmor", "AutoPot", "TargetStrafe", "AntiBot", "TickShift", "HitBox", "TPAura", "Ignite", "AutoSoup", "Killaura", "AimAssist", "TriggerBot", "Backstab", "CrystalAura", "AnchorAura", "BedAura", "BlockAura"],
    "MOVEMENT": ["Speed", "Flight", "Step", "Sprint", "LongJump", "HighJump", "Jesus", "NoSlow", "Spider", "Timer", "FastDrop", "Phase", "Blink", "SafeWalk", "AutoWalk", "ElytraFly", "IceSpeed", "WaterSpeed", "FastClimb", "Glide", "Levitation", "AntiFall", "SlimeJump"],
    "RENDER": ["ESP", "Tracers", "Chams", "Fullbright", "NameTags", "HUD", "ClickGUI", "BlockOutline", "Animations", "TargetHUD", "ItemESP", "Waypoints", "Freecam", "XRay", "NoRender", "CameraClip", "ViewModel", "Crosshair", "Radar", "Breadcrumbs", "HealthESP", "HoleESP", "LogoutSpots"],
    "PLAYER": ["AutoTool", "FastPlace", "FastBreak", "NoFall", "Scaffold", "ChestStealer", "InvManager", "AutoRespawn", "AntiCactus", "Derp", "AutoFish", "GhostHand", "AutoEat", "AutoMine", "AntiHunger", "AntiFire", "AutoHeal", "FastUse", "XPSpammer", "AutoGapple"],
    "WORLD": ["Nuker", "Weather", "Time", "Scammer", "PingSpoof", "AntiVoid", "Spammer", "Fucker", "Breaker", "Planter", "SignCrash", "BookCrash", "ChunkLoader", "BlockESP", "Search", "CaveFinder", "BaseFinder", "StashFinder"],
    "EXPLOIT": ["Disabler", "Crasher", "AntiKick", "PacketLogger", "BedrockESP", "Dupe", "ServerCrasher", "ConsoleSpammer", "PluginLogger", "ForceOP", "Bypass", "AntiBan", "UUIDSpoof", "IPLeaks", "LagSwitch"],
    "GHOST": ["WTap", "STap", "BlockHit", "LegitAura", "FakeLag", "DelayModifier", "Eagle"],
    "MISC": ["MiddleClickFriend", "DiscordRPC", "Announcer", "ChatBypass", "AntiSpam", "ChatFilter", "AutoTPA", "AutoLog", "FakeHacker", "Panic", "SelfDestruct", "MusicPlayer", "NoteBot"]
}

settings_pool = [
    {"name": "Mode", "type": "mode", "options": ["Normal", "Bypass", "Strict", "Fast"]},
    {"name": "Speed", "type": "slider", "min": 1, "max": 10},
    {"name": "Range", "type": "slider", "min": 3, "max": 6},
    {"name": "Delay", "type": "slider", "min": 0, "max": 1000},
    {"name": "Silent", "type": "toggle"},
    {"name": "ThroughWalls", "type": "toggle"},
    {"name": "AutoJump", "type": "toggle"},
    {"name": "Target", "type": "mode", "options": ["Players", "Mobs", "Animals", "All"]},
    {"name": "Priority", "type": "mode", "options": ["Health", "Distance", "Armor"]},
    {"name": "Invert", "type": "toggle"}
]

random.seed(42)

all_modules = []

for cat, mods in module_names.items():
    cat_dir = os.path.join(java_src_dir, cat.lower())
    os.makedirs(cat_dir, exist_ok=True)
    
    for mod in mods:
        class_name = mod
        all_modules.append((cat, class_name))
        
        with open(os.path.join(cat_dir, f"{class_name}Module.java"), "w") as f:
            f.write(f"""package com.LegitSkillIssue.client.module.{cat.lower()};

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;

public class {class_name}Module extends Module {{
    public {class_name}Module() {{
        super("{class_name}", "{class_name} module for {cat}", Category.{cat});
    }}
}}
""")

# write ModuleManager.java
manager_code = """package com.LegitSkillIssue.client.module;

import java.util.ArrayList;
import java.util.List;
"""

for cat, mod in all_modules:
    manager_code += f"import com.LegitSkillIssue.client.module.{cat.lower()}.{mod}Module;\n"

manager_code += """
public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private List<Module> modules = new ArrayList<>();
    
    public void init() {
"""

for cat, mod in all_modules:
    manager_code += f"        modules.add(new {mod}Module());\n"

manager_code += """    }
    
    public List<Module> getModules() { return modules; }
}
"""

with open(os.path.join(java_src_dir, "ModuleManager.java"), "w") as f:
    f.write(manager_code)

# Generate HTML panels
html_panels = ""
import math

categories_list = list(module_names.keys())
for i, cat in enumerate(categories_list):
    mods = module_names[cat]
    left = 50 + (i % 4) * 260
    top = 80 + math.floor(i / 4) * 380
    
    html_panels += f'''
        <div class="panel absolute w-[240px] flex flex-col bg-panel backdrop-blur-md border border-panel-border rounded-xl shadow-[0_12px_32px_rgba(0,0,0,0.5)] transition-all duration-200" style="top: {top}px; left: {left}px;">
            <div class="panel-header flex justify-between items-center p-3.5 bg-black/40 border-b border-panel-border rounded-t-xl cursor-move text-sm font-medium">
                <span>{cat.capitalize()}</span>
                <span class="panel-arrow text-[10px] text-dim transition-transform duration-300">▼</span>
            </div>
            <div class="panel-content overflow-y-auto overflow-x-hidden" style="max-height: 300px;">
                <div class="module-list py-2">
'''
    
    for mod in mods:
        s1, s2 = random.sample(settings_pool, 2)
        settings_html = ""
        for s in [s1, s2]:
            if s["type"] == "toggle":
                settings_html += f'''
                            <div class="setting-item relative flex flex-col gap-1.5 py-1.5 text-xs text-dim">
                                <div class="flex justify-between items-center">{s["name"]}
                                    <div class="toggle-switch w-7 h-3.5 bg-white/15 rounded-full relative cursor-pointer" onclick="this.classList.toggle('on')"></div>
                                </div>
                            </div>'''
            elif s["type"] == "slider":
                settings_html += f'''
                            <div class="setting-item relative flex flex-col gap-1.5 py-1.5 text-xs text-dim">
                                <div class="flex justify-between items-center">{s["name"]} <span class="text-white bg-black/40 px-1.5 border border-white/5 rounded">{s["min"]}</span></div>
                                <div class="slider-container w-full py-1 cursor-pointer relative">
                                    <div class="h-1 bg-white/10 w-full rounded relative">
                                        <div class="slider-fill h-full bg-accent rounded shadow-[0_0_6px_var(--accent-glow)]" style="width: 50%;"></div>
                                        <div class="slider-handle absolute top-1/2 left-[50%] w-3 h-3 bg-white rounded-full shadow-md -translate-x-1/2 -translate-y-1/2"></div>
                                    </div>
                                </div>
                            </div>'''
            elif s["type"] == "mode":
                settings_html += f'''
                            <div class="setting-item relative flex flex-col gap-1.5 py-1.5 text-xs text-dim">
                                <div class="flex justify-between items-center">{s["name"]}
                                    <div class="bg-black/40 px-2 py-1 rounded-md flex items-center gap-1.5 border border-white/5 hover:bg-white/10 cursor-pointer text-white">{s["options"][0]} <span class="text-[8px] text-dim">▼</span></div>
                                </div>
                            </div>'''

        html_panels += f'''
                    <div class="module mx-2 my-0.5 rounded-md transition-all duration-300">
                        <div class="module-header flex justify-between items-center p-2.5 rounded-md relative bg-transparent hover:bg-white/5 cursor-pointer text-[13px]" onmousedown="handleModuleClick(event, this)">
                            <div class="flex items-center gap-2"><span class="ml-2 module-name">{mod}</span></div>
                            <span class="indicator text-[18px] leading-none text-dim transition-transform duration-300">+</span>
                        </div>
                        <div class="settings-branch relative pl-[26px] pr-3 overflow-hidden" onclick="event.stopPropagation()">
                            {settings_html}
                        </div>
                    </div>
'''
    html_panels += '''
                </div>
            </div>
        </div>
'''

with open("prototype_gui.html", "r", encoding="utf-8") as f:
    content = f.read()

start_marker = '<div class="relative w-full h-screen z-10">'
end_marker = '<!-- Watermark (Chữ ký) -->'

start_idx = content.find(start_marker)
end_idx = content.find(end_marker)

if start_idx != -1 and end_idx != -1:
    new_content = content[:start_idx + len(start_marker)] + "\n" + html_panels + "\n    </div>\n\n    " + content[end_idx:]
    with open("prototype_gui.html", "w", encoding="utf-8") as f:
        f.write(new_content)
    print(f"Generated {len(all_modules)} modules successfully.")
else:
    print("Error: Could not inject HTML.")
