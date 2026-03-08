package com.nocrash.render;

import com.nocrash.config.NoCrashConfig;

public final class ParticleRenderLimiter {
	private static int particlesThisFrame;

	private ParticleRenderLimiter() {
	}

	public static void beginFrame() {
		particlesThisFrame = 0;
	}

	public static boolean shouldAllowParticle() {
		if (!NoCrashConfig.shouldLimitRenderedParticles()) {
			return true;
		}

		int limit = NoCrashConfig.getParticleRenderLimit();
		if (particlesThisFrame >= limit) {
			return false;
		}

		particlesThisFrame++;
		return true;
	}
}
