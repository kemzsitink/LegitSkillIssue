package com.client.legitskillissue.command;

import net.minecraft.util.EnumChatFormatting;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("Help", "Shows a list of all commands", "help");
    }

    @Override
    public void execute(String[] args) {
        CommandManager.addChatMessage(EnumChatFormatting.BLUE + "Available Commands:");
        for (Command cmd : CommandManager.INSTANCE.getCommands()) {
            CommandManager.addChatMessage(EnumChatFormatting.AQUA + CommandManager.INSTANCE.getPrefix() + cmd.getName().toLowerCase() + 
                EnumChatFormatting.GRAY + " - " + cmd.getDescription());
        }
    }
}
