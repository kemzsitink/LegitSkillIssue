package com.client.legitskillissue.module;

import com.client.legitskillissue.module.impl.combat.AutoClickerMod;
import com.client.legitskillissue.module.impl.combat.AutoPotMod;
import com.client.legitskillissue.module.impl.combat.AutoSoupMod;
import com.client.legitskillissue.module.impl.combat.CriticalsMod;
import com.client.legitskillissue.module.impl.combat.FastBowMod;
import com.client.legitskillissue.module.impl.combat.ReachMod;
import com.client.legitskillissue.module.impl.combat.SilentAuraMod;
import com.client.legitskillissue.module.impl.combat.TriggerBotMod;
import com.client.legitskillissue.module.impl.exploit.DisablerMod;
import com.client.legitskillissue.module.impl.misc.AimAssistMod;
import com.client.legitskillissue.module.impl.misc.AntiBotMod;
import com.client.legitskillissue.module.impl.misc.AntiVoidMod;
import com.client.legitskillissue.module.impl.misc.BacktrackMod;
import com.client.legitskillissue.module.impl.misc.BlockHitMod;
import com.client.legitskillissue.module.impl.misc.FastPlaceMod;
import com.client.legitskillissue.module.impl.misc.FlightMod;
import com.client.legitskillissue.module.impl.misc.FreecamMod;
import com.client.legitskillissue.module.impl.misc.HitBoxMod;
import com.client.legitskillissue.module.impl.misc.HitDelayFixMod;
import com.client.legitskillissue.module.impl.misc.InvMoveMod;
import com.client.legitskillissue.module.impl.misc.ScaffoldMod;
import com.client.legitskillissue.module.impl.misc.TargetSelectorMod;
import com.client.legitskillissue.module.impl.misc.TimerMod;
import com.client.legitskillissue.module.impl.misc.VelocityMod;
import com.client.legitskillissue.module.impl.misc.WTapMod;
import com.client.legitskillissue.module.impl.movement.AirJumpMod;
import com.client.legitskillissue.module.impl.movement.EagleMod;
import com.client.legitskillissue.module.impl.movement.JesusMod;
import com.client.legitskillissue.module.impl.movement.KeepSprintMod;
import com.client.legitskillissue.module.impl.movement.NoFallMod;
import com.client.legitskillissue.module.impl.movement.NoSlowMod;
import com.client.legitskillissue.module.impl.movement.NoWebMod;
import com.client.legitskillissue.module.impl.movement.ParkourMod;
import com.client.legitskillissue.module.impl.movement.SpeedMod;
import com.client.legitskillissue.module.impl.movement.SpiderMod;
import com.client.legitskillissue.module.impl.movement.SprintMod;
import com.client.legitskillissue.module.impl.movement.StepMod;
import com.client.legitskillissue.module.impl.player.AutoArmorMod;
import com.client.legitskillissue.module.impl.player.AutoBlinkMod;
import com.client.legitskillissue.module.impl.player.AutoDisconnectMod;
import com.client.legitskillissue.module.impl.player.AutoRespawnMod;
import com.client.legitskillissue.module.impl.player.AutoToolMod;
import com.client.legitskillissue.module.impl.player.ChestStealerMod;
import com.client.legitskillissue.module.impl.player.CreativeModeMod;
import com.client.legitskillissue.module.impl.player.DerpMod;
import com.client.legitskillissue.module.impl.player.FastDropMod;
import com.client.legitskillissue.module.impl.player.FastEatMod;
import com.client.legitskillissue.module.impl.player.InventoryManagerMod;
import com.client.legitskillissue.module.impl.player.SneakMod;
import com.client.legitskillissue.module.impl.render.BreadcrumbsMod;
import com.client.legitskillissue.module.impl.render.ChamsMod;
import com.client.legitskillissue.module.impl.render.ChestESPMod;
import com.client.legitskillissue.module.impl.render.FullbrightMod;
import com.client.legitskillissue.module.impl.render.NameProtectMod;
import com.client.legitskillissue.module.impl.render.NametagsMod;
import com.client.legitskillissue.module.impl.render.NoHurtCamMod;
import com.client.legitskillissue.module.impl.render.PlayerESPMod;
import com.client.legitskillissue.module.impl.render.TargetHUDMod;
import com.client.legitskillissue.module.impl.render.TracersMod;
import com.client.legitskillissue.module.impl.render.XRayMod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.Packet;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        modules.add(new AimAssistMod());
        modules.add(new AirJumpMod());
        modules.add(new AntiBotMod());
        modules.add(new AntiVoidMod());
        modules.add(new AutoArmorMod());
        modules.add(new AutoBlinkMod());
        modules.add(new AutoClickerMod());
        modules.add(new AutoDisconnectMod());
        modules.add(new AutoPotMod());
        modules.add(new AutoRespawnMod());
        modules.add(new AutoSoupMod());
        modules.add(new AutoToolMod());
        modules.add(new BacktrackMod());
        modules.add(new BlockHitMod());
        modules.add(new BreadcrumbsMod());
        modules.add(new ChamsMod());
        modules.add(new ChestESPMod());
        modules.add(new ChestStealerMod());
        modules.add(new CreativeModeMod());
        modules.add(new CriticalsMod());
        modules.add(new DerpMod());
        modules.add(new DisablerMod());
        modules.add(new EagleMod());
        modules.add(new FastBowMod());
        modules.add(new FastDropMod());
        modules.add(new FastEatMod());
        modules.add(new FastPlaceMod());
        modules.add(new FlightMod());
        modules.add(new FreecamMod());
        modules.add(new FullbrightMod());
        modules.add(new HitBoxMod());
        modules.add(new HitDelayFixMod());
        modules.add(new InvMoveMod());
        modules.add(new InventoryManagerMod());
        modules.add(new JesusMod());
        modules.add(new KeepSprintMod());
        modules.add(new NameProtectMod());
        modules.add(new NametagsMod());
        modules.add(new NoFallMod());
        modules.add(new NoHurtCamMod());
        modules.add(new NoSlowMod());
        modules.add(new NoWebMod());
        modules.add(new ParkourMod());
        modules.add(new PlayerESPMod());
        modules.add(new ReachMod());
        modules.add(new ScaffoldMod());
        modules.add(new SilentAuraMod());
        modules.add(new SneakMod());
        modules.add(new SpeedMod());
        modules.add(new SpiderMod());
        modules.add(new SprintMod());
        modules.add(new StepMod());
        modules.add(new TargetHUDMod());
        modules.add(new TargetSelectorMod());
        modules.add(new TimerMod());
        modules.add(new TracersMod());
        modules.add(new TriggerBotMod());
        modules.add(new VelocityMod());
        modules.add(new WTapMod());
        modules.add(new XRayMod());
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (m.getClass() == clazz) return (T) m;
        }
        return null;
    }

    public void onKey(int key) {
        for (Module m : modules) {
            if (m.getKeybind() == key) m.toggle();
        }
    }

    public void onMouseClick(net.minecraftforge.client.event.MouseEvent event) {
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
