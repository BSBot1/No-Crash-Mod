package com.nocrash.render;

import com.nocrash.config.NoCrashConfig;

public final class BlockEntityRenderLimiter {
	private static int renderedBlockEntitiesThisFrame;

	private BlockEntityRenderLimiter() {
	}

	public static void beginFrame() {
		renderedBlockEntitiesThisFrame = 0;
	}

	public static boolean shouldRenderNextBlockEntity() {
		if (!NoCrashConfig.shouldLimitRenderedBlockEntities()) {
			return true;
		}

		int limit = NoCrashConfig.getBlockEntityRenderLimit();
		if (renderedBlockEntitiesThisFrame >= limit) {
			return false;
		}

		renderedBlockEntitiesThisFrame++;
		return true;
	}
}
