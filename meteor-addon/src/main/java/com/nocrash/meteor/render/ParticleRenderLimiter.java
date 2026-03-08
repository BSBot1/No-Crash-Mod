package com.nocrash.meteor.render;

import com.nocrash.meteor.state.NoCrashState;

public final class ParticleRenderLimiter {
	private static int particlesThisFrame;

	private ParticleRenderLimiter() {
	}

	public static void beginFrame() {
		particlesThisFrame = 0;
	}

	public static boolean shouldAllowParticle() {
		if (!NoCrashState.shouldLimitRenderedParticles()) {
			return true;
		}

		int limit = NoCrashState.getParticleRenderLimit();
		if (particlesThisFrame >= limit) {
			return false;
		}

		particlesThisFrame++;
		return true;
	}
}
