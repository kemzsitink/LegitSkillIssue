package com.LegitSkillIssue.client;

import com.LegitSkillIssue.client.module.Module;
import com.LegitSkillIssue.client.module.ModuleManager;
import com.google.gson.JsonObject;

public class GuiBridge {
    public static String handleQuery(String query) {
        if (query.equals("get_states")) {
            JsonObject states = new JsonObject();
            for (Module m : ModuleManager.INSTANCE.getModules()) {
                states.addProperty(m.getName(), m.isEnabled());
            }
            return states.toString();
        }
        
        if (query.startsWith("toggle:")) {
            String name = query.substring(7);
            for (Module m : ModuleManager.INSTANCE.getModules()) {
                if (m.getName().equalsIgnoreCase(name)) {
                    m.toggle();
                    return "toggled";
                }
            }
        }
        
        return "unknown_query";
    }
}
