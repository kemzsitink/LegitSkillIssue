package com.client.legitskillissue.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages the friends list.
 * Friends are players who should be ignored by combat modules.
 */
public class FriendManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "legitskillissue");
    private static final File FRIENDS_FILE = new File(CONFIG_DIR, "friends.json");

    private static final Set<String> friends = new HashSet<>();

    static {
        load();
    }

    public static void addFriend(String name) {
        friends.add(name.toLowerCase());
        save();
    }

    public static void removeFriend(String name) {
        friends.remove(name.toLowerCase());
        save();
    }

    public static boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }

    public static Set<String> getFriends() {
        return friends;
    }

    public static void save() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        try (FileWriter writer = new FileWriter(FRIENDS_FILE)) {
            GSON.toJson(friends, writer);
        } catch (IOException e) {
            System.err.println("[FriendManager] Failed to save friends: " + e.getMessage());
        }
    }

    public static void load() {
        if (!FRIENDS_FILE.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(FRIENDS_FILE)) {
            Set<String> loaded = GSON.fromJson(reader, new TypeToken<Set<String>>(){}.getType());
            if (loaded != null) {
                friends.clear();
                friends.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("[FriendManager] Failed to load friends: " + e.getMessage());
        }
    }
}
