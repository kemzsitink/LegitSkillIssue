package com.LegitSkillIssue.client;

import net.fabricmc.api.ClientModInitializer;
import com.LegitSkillIssue.client.module.ModuleManager;

public class LegitSkillIssueClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModuleManager.INSTANCE.init();
	}
}