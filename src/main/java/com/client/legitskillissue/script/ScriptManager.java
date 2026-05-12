package com.client.legitskillissue.script;

import com.client.legitskillissue.module.Category;
import com.client.legitskillissue.module.Module;
import com.client.legitskillissue.utils.Logger;
import net.minecraft.client.Minecraft;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

/**
 * Advanced Scripting Engine using Nashorn (JavaScript).
 * Allows users to load custom modules at runtime.
 */
public class ScriptManager {

    public static final ScriptManager INSTANCE = new ScriptManager();
    private final ScriptEngine engine;
    private final File scriptDir;

    private ScriptManager() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
        this.scriptDir = new File(Minecraft.getMinecraft().mcDataDir, "legitskillissue/scripts");
        if (!scriptDir.exists()) scriptDir.mkdirs();
    }

    public void loadScripts() {
        if (engine == null) {
            Logger.getLogger(ScriptManager.class).error("Nashorn ScriptEngine not found!");
            return;
        }

        File[] files = scriptDir.listFiles((dir, name) -> name.endsWith(".js"));
        if (files == null) return;

        for (File file : files) {
            try {
                engine.eval(new FileReader(file));
                Logger.getLogger(ScriptManager.class).info("Loaded script: " + file.getName());
            } catch (Exception e) {
                Logger.getLogger(ScriptManager.class).error("Failed to load script: " + file.getName(), e);
            }
        }
    }

    /**
     * Internal class for JS to interface with the mod.
     */
    public static class ScriptModule extends Module {
        private final String scriptName;

        public ScriptModule(String name, Category category) {
            super(name, category);
            this.scriptName = name;
        }
        
        // This is where JS would override methods like onUpdate
    }
}
