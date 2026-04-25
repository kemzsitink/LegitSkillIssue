package com.LegitSkillIssue.client.module.misc;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class MusicPlayerModule extends Module {
    public MusicPlayerModule() {
        super("MusicPlayer", "Plays music discs for you.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null && mc.world != null) {
            mc.world.playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), 
                SoundEvents.MUSIC_DISC_PIGSTEP.value(), SoundCategory.RECORDS, 1.0f, 1.0f, false);
        }
        setEnabled(false);
    }
}
