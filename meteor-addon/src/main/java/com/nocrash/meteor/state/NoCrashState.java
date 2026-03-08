package com.nocrash.meteor.state;

import com.nocrash.meteor.modules.AntiCrashModule;
import meteordevelopment.meteorclient.systems.modules.Modules;

public final class NoCrashState {
	public static final int ENTITY_RENDER_LIMIT_INFINITE = 1001;
	public static final int PARTICLE_RENDER_LIMIT_INFINITE = 10001;

	private NoCrashState() {
	}

	public static boolean isAntiCrashEnabled() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive();
	}

	public static boolean shouldBlockGuardianCrash() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.antiGuardianCrash.get();
	}

	public static boolean shouldBlockLoudNoises() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.blockLoudNoises.get();
	}

	public static boolean shouldHideLongNametags() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.hideLongNametags.get();
	}

	public static boolean shouldBypassInteractionEntities() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.interactionBypass.get();
	}

	public static boolean shouldGuardRamAndPackets() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.ramPacketGuard.get();
	}

	public static boolean shouldGuardStructureOutlines() {
		AntiCrashModule module = getModule();
		return module != null && module.isActive() && module.structureOutlineGuard.get();
	}

	public static int getEntityRenderLimit() {
		AntiCrashModule module = getModule();
		if (module == null || !module.isActive()) {
			return ENTITY_RENDER_LIMIT_INFINITE;
		}

		int limit = module.entityRenderLimit.get();
		return Math.max(0, Math.min(ENTITY_RENDER_LIMIT_INFINITE, limit));
	}

	public static boolean shouldLimitRenderedEntities() {
		return isAntiCrashEnabled() && getEntityRenderLimit() < ENTITY_RENDER_LIMIT_INFINITE;
	}

	public static int getParticleRenderLimit() {
		AntiCrashModule module = getModule();
		if (module == null || !module.isActive()) {
			return PARTICLE_RENDER_LIMIT_INFINITE;
		}

		int limit = module.particleRenderLimit.get();
		return Math.max(0, Math.min(PARTICLE_RENDER_LIMIT_INFINITE, limit));
	}

	public static boolean shouldLimitRenderedParticles() {
		return isAntiCrashEnabled() && getParticleRenderLimit() < PARTICLE_RENDER_LIMIT_INFINITE;
	}

	private static AntiCrashModule getModule() {
		try {
			Modules modules = Modules.get();
			if (modules == null) {
				return null;
			}

			return modules.get(AntiCrashModule.class);
		} catch (Throwable ignored) {
			return null;
		}
	}
}
