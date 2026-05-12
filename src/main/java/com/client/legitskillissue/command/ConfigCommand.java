package com.client.legitskillissue.command;

import com.client.legitskillissue.utils.ConfigManager;
import net.minecraft.util.EnumChatFormatting;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("Config", "Manages configurations", "config <save|load>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: " + getUsage());
            return;
        }

        if (args[0].equalsIgnoreCase("save")) {
            ConfigManager.save();
            CommandManager.addChatMessage(EnumChatFormatting.GREEN + "Configuration saved!");
        } else if (args[0].equalsIgnoreCase("load")) {
            ConfigManager.load();
            CommandManager.addChatMessage(EnumChatFormatting.GREEN + "Configuration loaded!");
        } else {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: " + getUsage());
        }
    }
}
