package com.client.legitskillissue.command;

import com.client.legitskillissue.utils.FriendManager;
import net.minecraft.util.EnumChatFormatting;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("Friend", "Manages the friends list", "friend <add|remove|list> [name]", "f");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: " + getUsage());
            return;
        }

        String action = args[0].toLowerCase();

        if (action.equals("add")) {
            if (args.length < 2) {
                CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: friend add <name>");
                return;
            }
            String name = args[1];
            if (FriendManager.isFriend(name)) {
                CommandManager.addChatMessage(EnumChatFormatting.YELLOW + name + " is already your friend.");
            } else {
                FriendManager.addFriend(name);
                CommandManager.addChatMessage(EnumChatFormatting.GREEN + "Added " + name + " to friends.");
            }
        } else if (action.equals("remove") || action.equals("del")) {
            if (args.length < 2) {
                CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: friend remove <name>");
                return;
            }
            String name = args[1];
            if (!FriendManager.isFriend(name)) {
                CommandManager.addChatMessage(EnumChatFormatting.RED + name + " is not your friend.");
            } else {
                FriendManager.removeFriend(name);
                CommandManager.addChatMessage(EnumChatFormatting.GREEN + "Removed " + name + " from friends.");
            }
        } else if (action.equals("list")) {
            if (FriendManager.getFriends().isEmpty()) {
                CommandManager.addChatMessage(EnumChatFormatting.YELLOW + "Your friends list is empty.");
            } else {
                CommandManager.addChatMessage(EnumChatFormatting.BLUE + "Friends List:");
                for (String name : FriendManager.getFriends()) {
                    CommandManager.addChatMessage(EnumChatFormatting.GRAY + "- " + name);
                }
            }
        } else {
            CommandManager.addChatMessage(EnumChatFormatting.RED + "Usage: " + getUsage());
        }
    }
}
