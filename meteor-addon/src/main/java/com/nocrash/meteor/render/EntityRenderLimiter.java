package com.nocrash.meteor.render;

import com.nocrash.meteor.state.NoCrashState;

public final class EntityRenderLimiter {
	private static int renderedEntitiesThisFrame;

	private EntityRenderLimiter() {
	}

	public static void beginFrame() {
		renderedEntitiesThisFrame = 0;
	}

	public static boolean shouldRenderNextEntity() {
		if (!NoCrashState.shouldLimitRenderedEntities()) {
			return true;
		}

		int limit = NoCrashState.getEntityRenderLimit();
		if (renderedEntitiesThisFrame >= limit) {
			return false;
		}

		renderedEntitiesThisFrame++;
		return true;
	}
}
