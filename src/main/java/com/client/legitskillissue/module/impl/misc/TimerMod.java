package com.client.legitskillissue.module.impl.misc;

import com.client.legitskillissue.event.EventTarget;
import com.client.legitskillissue.event.impl.EventUpdate;
import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.client.legitskillissue.utils.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import java.lang.reflect.Field;

public class TimerMod extends Module {

    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Timer speed multiplier", 0.1f, 10f, 0.1f, 2f));
    
    private static final Field TIMER = ReflectionUtil.findField(Minecraft.class, "timer", "field_71428_T");

    public TimerMod() {
        super("Timer", Category.MISC);
    }

    private Timer getTimer() {
        try {
            if (TIMER != null) return (Timer) TIMER.get(mc);
        } catch (Exception e) {}
        return null;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer != null) {
            Timer timer = getTimer();
            if (timer != null) timer.timerSpeed = speed.getValue();
        }
    }

    @Override
    protected void onDisable() {
        Timer timer = getTimer();
        if (timer != null) timer.timerSpeed = 1.0f;
    }
}
