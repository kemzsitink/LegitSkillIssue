package com.client.legitskillissue.utils;

import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.module.ModuleManager;
import com.client.legitskillissue.module.setting.BooleanSetting;
import com.client.legitskillissue.module.setting.ModeSetting;
import com.client.legitskillissue.module.setting.NumberSetting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages saving and loading of module configurations.
 * Persists module states (enabled/disabled), keybinds, and settings to a JSON file.
 */
public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "legitskillissue");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");

    private static boolean loading = false;

    /**
     * Saves the current configuration to disk.
     */
    public static void save() {
        if (loading) return;
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }

        JsonObject configJson = new JsonObject();
        JsonObject modulesJson = new JsonObject();

        for (Module module : ModuleManager.INSTANCE.getModules()) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("enabled", module.isEnabled());
            moduleJson.addProperty("keybind", module.getKeybind());

            JsonObject settingsJson = new JsonObject();
            for (BooleanSetting s : module.getBooleanSettings()) {
                settingsJson.addProperty(s.getName(), s.getValue());
            }
            for (NumberSetting s : module.getSettings()) {
                settingsJson.addProperty(s.getName(), s.getValue());
            }
            for (ModeSetting s : module.getModeSettings()) {
                settingsJson.addProperty(s.getName(), s.getMode());
            }
            moduleJson.add("settings", settingsJson);

            modulesJson.add(module.getName(), moduleJson);
        }

        configJson.add("modules", modulesJson);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configJson, writer);
        } catch (IOException e) {
            System.err.println("[ConfigManager] Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Loads the configuration from disk.
     */
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            return;
        }

        loading = true;
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject configJson = GSON.fromJson(reader, JsonObject.class);
            if (configJson == null || !configJson.has("modules")) {
                loading = false;
                return;
            }

            JsonObject modulesJson = configJson.getAsJsonObject("modules");

            for (Module module : ModuleManager.INSTANCE.getModules()) {
                if (modulesJson.has(module.getName())) {
                    JsonObject moduleJson = modulesJson.getAsJsonObject(module.getName());

                    if (moduleJson.has("enabled")) {
                        module.setEnabled(moduleJson.get("enabled").getAsBoolean());
                    }
                    if (moduleJson.has("keybind")) {
                        module.setKeybind(moduleJson.get("keybind").getAsInt());
                    }

                    if (moduleJson.has("settings")) {
                        JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
                        
                        for (BooleanSetting s : module.getBooleanSettings()) {
                            if (settingsJson.has(s.getName())) {
                                s.setValue(settingsJson.get(s.getName()).getAsBoolean());
                            }
                        }
                        for (NumberSetting s : module.getSettings()) {
                            if (settingsJson.has(s.getName())) {
                                s.setValue(settingsJson.get(s.getName()).getAsFloat());
                            }
                        }
                        for (ModeSetting s : module.getModeSettings()) {
                            if (settingsJson.has(s.getName())) {
                                s.setMode(settingsJson.get(s.getName()).getAsString());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ConfigManager] Failed to load config: " + e.getMessage());
        } finally {
            loading = false;
        }
    }
}
