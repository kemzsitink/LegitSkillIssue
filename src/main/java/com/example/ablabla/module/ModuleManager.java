package com.example.ablabla.module;

import com.example.ablabla.module.impl.*;
import com.example.ablabla.module.impl.PlayerESPMod;
import net.minecraft.network.Packet;
import net.minecraftforge.client.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    public static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat
        modules.add(new WTapMod());
        modules.add(new VelocityMod());
        modules.add(new NoSlowMod());
        modules.add(new AutoClickerMod());
        modules.add(new TriggerBotMod());
        modules.add(new FastPlaceMod());
        modules.add(new AimAssistMod());
        modules.add(new EagleMod());
        modules.add(new SprintMod());
        modules.add(new AirJumpMod());
        modules.add(new NoFallMod());
        modules.add(new BlockHitMod());
        modules.add(new HitBoxMod());
        modules.add(new BacktrackMod());
        modules.add(new AutoBlinkMod());
        modules.add(new ReachMod());
        modules.add(new HitDelayFixMod());
        modules.add(new KeepSprintMod());
        modules.add(new FastEatMod());
        modules.add(new SilentAuraMod());
        // Render
        modules.add(new PlayerESPMod());
        modules.add(new ChamsMod());
        modules.add(new FullbrightMod());
        modules.add(new NoHurtCamMod());
        // Player
        modules.add(new InvMoveMod());
        modules.add(new ChestStealerMod());
        modules.add(new FlightMod());
        modules.add(new CreativeModeMod());
        modules.add(new NetworkStressTester());
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) return (T) m;
        }
        return null;
    }

    /** Called on KeyInputEvent — toggle module if its keybind matches. */
    public void onKeyPress(int keyCode) {
        for (Module m : modules) {
            if (m.getKeybind() != org.lwjgl.input.Keyboard.KEY_NONE
                    && m.getKeybind() == keyCode) {
                m.toggle();
            }
        }
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }

    public void onMouseClick(MouseEvent event) {
        for (Module m : modules) {
            if (m.isEnabled()) m.onMouseClick(event);
        }
    }

    public boolean onPacketSend(Packet<?> packet) {
        for (Module m : modules) {
            if (m.isEnabled() && m.onPacketSend(packet)) return true;
        }
        return false;
    }

    public boolean onPacketReceive(Packet<?> packet) {
        for (Module m : modules) {
            if (m.isEnabled() && m.onPacketReceive(packet)) return true;
        }
        return false;
    }
}
