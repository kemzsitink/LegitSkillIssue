package com.example.ablabla.module;

import com.example.ablabla.module.impl.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new WTapMod());
        modules.add(new VelocityMod());
        modules.add(new NoSlowMod());
        modules.add(new AutoClickerMod());
        modules.add(new FastPlaceMod());
        modules.add(new AimAssistMod());
        modules.add(new EagleMod());
        modules.add(new SprintMod());
        modules.add(new BlockHitMod());
        modules.add(new HitBoxMod());
        modules.add(new BacktrackMod());
        modules.add(new ReachMod());
        // Modules like Killaura would be added here
    }

    public List<Module> getModules() { return modules; }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }

    public void onMouseClick(net.minecraftforge.client.event.MouseEvent event) {
        for (Module m : modules) {
            if (m.isEnabled()) m.onMouseClick(event);
        }
    }

    public boolean onPacketSend(net.minecraft.network.Packet<?> packet) {
        for (Module m : modules) {
            if (m.isEnabled() && m.onPacketSend(packet)) return true;
        }
        return false;
    }

    public boolean onPacketReceive(net.minecraft.network.Packet<?> packet) {
        for (Module m : modules) {
            if (m.isEnabled() && m.onPacketReceive(packet)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) return (T) m;
        }
        return null;
    }
}
