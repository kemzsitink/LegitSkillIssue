package com.client.legitskillissue.command;

import com.client.legitskillissue.utils.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages chat commands for the client.
 */
public class CommandManager {

    public static final CommandManager INSTANCE = new CommandManager();
    private final List<Command> commands = new ArrayList<>();
    private final String prefix = ".";

    private CommandManager() {
        // Register commands here
        commands.add(new HelpCommand());
        commands.add(new FriendCommand());
        commands.add(new ToggleCommand());
        commands.add(new ConfigCommand());
        com.client.legitskillissue.event.EventBus.INSTANCE.register(this);
    }

    @com.client.legitskillissue.event.EventTarget
    public void onPacket(com.client.legitskillissue.event.impl.EventPacket event) {
        if (event.isSend && event.getPacket() instanceof net.minecraft.network.play.client.C01PacketChatMessage) {
            net.minecraft.network.play.client.C01PacketChatMessage p = (net.minecraft.network.play.client.C01PacketChatMessage) event.getPacket();
            if (handleCommand(p.getMessage())) {
                event.setCancelled(true);
            }
        }
    }

    public boolean handleCommand(String message) {
        if (!message.startsWith(prefix)) return false;

        String[] split = message.substring(prefix.length()).split(" ");
        String name = split[0];
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(name) || Arrays.asList(cmd.getAliases()).contains(name.toLowerCase())) {
                cmd.execute(args);
                return true;
            }
        }

        addChatMessage(EnumChatFormatting.RED + "Unknown command. Type " + prefix + "help for a list of commands.");
        return true;
    }

    public static void addChatMessage(String message) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        
        String formatted = EnumChatFormatting.BLUE + "[LegitSkillIssue] " + EnumChatFormatting.GRAY + message;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(formatted));
    }

    public List<Command> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }
}
