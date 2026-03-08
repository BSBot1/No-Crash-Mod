package com.nocrash.meteor;

import com.nocrash.meteor.modules.AntiCrashModule;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public final class NoCrashMeteorAddon extends MeteorAddon {
	public static final Logger LOG = LogUtils.getLogger();
	public static final Category CATEGORY = new Category("No Crash");

	@Override
	public void onInitialize() {
		LOG.info("Initializing No Crash Meteor Addon");
		Modules.get().add(new AntiCrashModule());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}

	@Override
	public String getPackage() {
		return "com.nocrash.meteor";
	}

	@Override
	public GithubRepo getRepo() {
		return new GithubRepo("BSBot1", "No-Crash-Mod");
	}
}
