package com.client.legitskillissue.command;

import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import net.minecraft.util.EnumChatFormatting;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("Toggle", "Toggles a module on or off", "toggle <module>", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: " + getUsage());
            return;
        }

        String moduleName = args[0];
        Module m = null;
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                m = module;
                break;
            }
        }

        if (m != null) {
            m.toggle();
            CommandManager.addChatMessage(m.getName() + " is now " + (m.isEnabled() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled"));
        } else {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Module not found: " + moduleName);
        }
    }
}
