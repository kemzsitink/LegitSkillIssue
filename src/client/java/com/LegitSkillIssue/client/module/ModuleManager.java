package com.LegitSkillIssue.client.module;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.LegitSkillIssue.client.module.combat.*;
import com.LegitSkillIssue.client.module.movement.*;
import com.LegitSkillIssue.client.module.render.*;
import com.LegitSkillIssue.client.module.player.*;
import com.LegitSkillIssue.client.module.world.*;
import com.LegitSkillIssue.client.module.exploit.*;
import com.LegitSkillIssue.client.module.ghost.*;
import com.LegitSkillIssue.client.module.misc.*;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private List<Module> modules = new ArrayList<>();
    
    public void init() {
        // Combat
        modules.add(new AuraModule());
        modules.add(new AutoClickerModule());
        modules.add(new VelocityModule());
        modules.add(new ReachModule());
        modules.add(new CriticalsModule());
        modules.add(new BowAimbotModule());
        modules.add(new AutoPotModule());
        modules.add(new TargetStrafeModule());
        modules.add(new AntiBotModule());
        modules.add(new TickShiftModule());
        modules.add(new HitBoxModule());
        modules.add(new TPAuraModule());
        modules.add(new IgniteModule());
        modules.add(new AutoSoupModule());
        modules.add(new KillauraModule());
        modules.add(new AimAssistModule());
        modules.add(new TriggerBotModule());
        modules.add(new BackstabModule());
        modules.add(new CrystalAuraModule());
        modules.add(new AnchorAuraModule());
        modules.add(new BedAuraModule());
        modules.add(new BlockAuraModule());

        // Movement
        modules.add(new SpeedModule());
        modules.add(new FlightModule());
        modules.add(new StepModule());
        modules.add(new SprintModule());
        modules.add(new LongJumpModule());
        modules.add(new HighJumpModule());
        modules.add(new JesusModule());
        modules.add(new NoSlowModule());
        modules.add(new SpiderModule());
        modules.add(new TimerModule());
        modules.add(new FastDropModule());
        modules.add(new PhaseModule());
        modules.add(new BlinkModule());
        modules.add(new SafeWalkModule());
        modules.add(new AutoWalkModule());
        modules.add(new ElytraFlyModule());
        modules.add(new IceSpeedModule());
        modules.add(new WaterSpeedModule());
        modules.add(new FastClimbModule());
        modules.add(new GlideModule());
        modules.add(new LevitationModule());
        modules.add(new AntiFallModule());
        modules.add(new SlimeJumpModule());

        // Render
        modules.add(new ESPModule());
        modules.add(new TracersModule());
        modules.add(new ChamsModule());
        modules.add(new FullbrightModule());
        modules.add(new NameTagsModule());
        modules.add(new HUDModule());
        modules.add(new ClickGUIModule());
        modules.add(new BlockOutlineModule());
        modules.add(new AnimationsModule());
        modules.add(new TargetHUDModule());
        modules.add(new ItemESPModule());
        modules.add(new WaypointsModule());
        modules.add(new FreecamModule());
        modules.add(new XRayModule());
        modules.add(new NoRenderModule());
        modules.add(new CameraClipModule());
        modules.add(new ViewModelModule());
        modules.add(new CrosshairModule());
        modules.add(new RadarModule());
        modules.add(new BreadcrumbsModule());
        modules.add(new HealthESPModule());
        modules.add(new HoleESPModule());
        modules.add(new LogoutSpotsModule());

        // Player
        modules.add(new AutoToolModule());
        modules.add(new AutoArmorModule()); // Now correctly in player package
        modules.add(new FastPlaceModule());
        modules.add(new FastBreakModule());
        modules.add(new NoFallModule());
        modules.add(new ScaffoldModule());
        modules.add(new ChestStealerModule());
        modules.add(new InvManagerModule());
        modules.add(new AutoRespawnModule());
        modules.add(new AntiCactusModule());
        modules.add(new DerpModule());
        modules.add(new AutoFishModule());
        modules.add(new GhostHandModule());
        modules.add(new AutoEatModule());
        modules.add(new AutoMineModule());
        modules.add(new AntiHungerModule());
        modules.add(new AntiFireModule());
        modules.add(new AutoHealModule());
        modules.add(new FastUseModule());
        modules.add(new XPSpammerModule());
        modules.add(new AutoGappleModule());

        // World
        modules.add(new NukerModule());
        modules.add(new WeatherModule());
        modules.add(new TimeModule());
        modules.add(new ScammerModule());
        modules.add(new PingSpoofModule());
        modules.add(new AntiVoidModule());
        modules.add(new SpammerModule());
        modules.add(new FuckerModule());
        modules.add(new BreakerModule());
        modules.add(new PlanterModule());
        modules.add(new SignCrashModule());
        modules.add(new BookCrashModule());
        modules.add(new ChunkLoaderModule());
        modules.add(new BlockESPModule());
        modules.add(new SearchModule());
        modules.add(new CaveFinderModule());
        modules.add(new BaseFinderModule());
        modules.add(new StashFinderModule());

        // Exploit
        modules.add(new DisablerModule());
        modules.add(new CrasherModule());
        modules.add(new AntiKickModule());
        modules.add(new PacketLoggerModule());
        modules.add(new BedrockESPModule());
        modules.add(new DupeModule());
        modules.add(new ServerCrasherModule());
        modules.add(new ConsoleSpammerModule());
        modules.add(new PluginLoggerModule());
        modules.add(new ForceOPModule());
        modules.add(new BypassModule());
        modules.add(new AntiBanModule());
        modules.add(new UUIDSpoofModule());
        modules.add(new IPLeaksModule());
        modules.add(new LagSwitchModule());

        // Ghost
        modules.add(new WTapModule());
        modules.add(new STapModule());
        modules.add(new BlockHitModule());
        modules.add(new LegitAuraModule());
        modules.add(new FakeLagModule());
        modules.add(new DelayModifierModule());
        modules.add(new EagleModule());

        // Misc
        modules.add(new MiddleClickFriendModule());
        modules.add(new DiscordRPCModule());
        modules.add(new AnnouncerModule());
        modules.add(new ChatBypassModule());
        modules.add(new AntiSpamModule());
        modules.add(new ChatFilterModule());
        modules.add(new AutoTPAModule());
        modules.add(new AutoLogModule());
        modules.add(new FakeHackerModule());
        modules.add(new PanicModule());
        modules.add(new SelfDestructModule());
        modules.add(new MusicPlayerModule());
        modules.add(new NoteBotModule());

        // Register Tick Event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                for (Module m : modules) {
                    if (m.isEnabled()) {
                        m.onTick();
                    }
                }
            }
        });
    }
    
    public List<Module> getModules() { return modules; }
}
