package com.nocrash.render;

import com.nocrash.config.NoCrashConfig;

public final class EntityRenderLimiter {
	private static int renderedEntitiesThisFrame = 0;

	private EntityRenderLimiter() {
	}

	public static void beginFrame() {
		renderedEntitiesThisFrame = 0;
	}

	public static boolean shouldRenderNextEntity() {
		if (!NoCrashConfig.shouldLimitRenderedEntities()) {
			return true;
		}

		int limit = NoCrashConfig.getEntityRenderLimit();
		if (renderedEntitiesThisFrame >= limit) {
			return false;
		}

		renderedEntitiesThisFrame++;
		return true;
	}
}
