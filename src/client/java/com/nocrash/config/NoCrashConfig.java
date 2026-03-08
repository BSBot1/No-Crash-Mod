package com.nocrash.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nocrash.NoCrashClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NoCrashConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nocrash.json");
	public static final int ENTITY_RENDER_LIMIT_INFINITE = 1001;
	public static final int PARTICLE_RENDER_LIMIT_INFINITE = 10001;

	private static boolean antiCrashEnabled = true;
	private static boolean antiGuardianCrashEnabled = true;
	private static boolean blockLoudNoisesEnabled = true;
	private static boolean hideLongNametagsEnabled = true;
	private static boolean interactionBypassEnabled = true;
	private static boolean ramPacketGuardEnabled = true;
	private static boolean structureOutlineGuardEnabled = true;
	private static int entityRenderLimit = ENTITY_RENDER_LIMIT_INFINITE;
	private static int particleRenderLimit = PARTICLE_RENDER_LIMIT_INFINITE;

	private NoCrashConfig() {
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
			ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
			if (loaded != null) {
				if (loaded.antiCrashEnabled != null) {
					antiCrashEnabled = loaded.antiCrashEnabled;
				}
				if (loaded.antiGuardianCrashEnabled != null) {
					antiGuardianCrashEnabled = loaded.antiGuardianCrashEnabled;
				} else if (loaded.blockElderGuardianEffect != null) {
					antiGuardianCrashEnabled = loaded.blockElderGuardianEffect;
				}
				if (loaded.blockLoudNoisesEnabled != null) {
					blockLoudNoisesEnabled = loaded.blockLoudNoisesEnabled;
				}
				if (loaded.hideLongNametagsEnabled != null) {
					hideLongNametagsEnabled = loaded.hideLongNametagsEnabled;
				}
				if (loaded.interactionBypassEnabled != null) {
					interactionBypassEnabled = loaded.interactionBypassEnabled;
				}
				if (loaded.ramPacketGuardEnabled != null) {
					ramPacketGuardEnabled = loaded.ramPacketGuardEnabled;
				}
				if (loaded.structureOutlineGuardEnabled != null) {
					structureOutlineGuardEnabled = loaded.structureOutlineGuardEnabled;
				}
				if (loaded.entityRenderLimit != null) {
					setEntityRenderLimit(loaded.entityRenderLimit);
				} else if (loaded.maxRenderedEntities != null) {
					setEntityRenderLimit(loaded.maxRenderedEntities);
				}
				if (loaded.particleRenderLimit != null) {
					setParticleRenderLimit(loaded.particleRenderLimit);
				} else if (loaded.maxRenderedParticles != null) {
					setParticleRenderLimit(loaded.maxRenderedParticles);
				}
			}
		} catch (Exception exception) {
			NoCrashClient.LOGGER.warn("Failed to read config, using defaults: {}", exception.getMessage());
		}
	}

	public static void save() {
		try {
			Path parent = CONFIG_PATH.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}

			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(new ConfigData(
					antiCrashEnabled,
					antiGuardianCrashEnabled,
					blockLoudNoisesEnabled,
					hideLongNametagsEnabled,
					interactionBypassEnabled,
					ramPacketGuardEnabled,
					structureOutlineGuardEnabled,
					entityRenderLimit,
					particleRenderLimit
				), writer);
			}
		} catch (IOException exception) {
			NoCrashClient.LOGGER.warn("Failed to write config: {}", exception.getMessage());
		}
	}

	public static boolean isAntiCrashEnabled() {
		return antiCrashEnabled;
	}

	public static void setAntiCrashEnabled(boolean enabled) {
		antiCrashEnabled = enabled;
	}

	public static boolean isAntiGuardianCrashEnabled() {
		return antiGuardianCrashEnabled;
	}

	public static void setAntiGuardianCrashEnabled(boolean enabled) {
		antiGuardianCrashEnabled = enabled;
	}

	public static boolean shouldBlockGuardianCrash() {
		return antiCrashEnabled && antiGuardianCrashEnabled;
	}

	public static boolean isBlockLoudNoisesEnabled() {
		return blockLoudNoisesEnabled;
	}

	public static void setBlockLoudNoisesEnabled(boolean enabled) {
		blockLoudNoisesEnabled = enabled;
	}

	public static boolean shouldBlockLoudNoises() {
		return antiCrashEnabled && blockLoudNoisesEnabled;
	}

	public static boolean isHideLongNametagsEnabled() {
		return hideLongNametagsEnabled;
	}

	public static void setHideLongNametagsEnabled(boolean enabled) {
		hideLongNametagsEnabled = enabled;
	}

	public static boolean shouldHideLongNametags() {
		return antiCrashEnabled && hideLongNametagsEnabled;
	}

	public static boolean isInteractionBypassEnabled() {
		return interactionBypassEnabled;
	}

	public static void setInteractionBypassEnabled(boolean enabled) {
		interactionBypassEnabled = enabled;
	}

	public static boolean shouldBypassInteractionEntities() {
		return antiCrashEnabled && interactionBypassEnabled;
	}

	public static boolean isRamPacketGuardEnabled() {
		return ramPacketGuardEnabled;
	}

	public static void setRamPacketGuardEnabled(boolean enabled) {
		ramPacketGuardEnabled = enabled;
	}

	public static boolean shouldGuardRamAndPackets() {
		return antiCrashEnabled && ramPacketGuardEnabled;
	}

	public static boolean isStructureOutlineGuardEnabled() {
		return structureOutlineGuardEnabled;
	}

	public static void setStructureOutlineGuardEnabled(boolean enabled) {
		structureOutlineGuardEnabled = enabled;
	}

	public static boolean shouldGuardStructureOutlines() {
		return antiCrashEnabled && structureOutlineGuardEnabled;
	}

	public static int getEntityRenderLimit() {
		return entityRenderLimit;
	}

	public static void setEntityRenderLimit(int limit) {
		entityRenderLimit = Math.max(0, Math.min(ENTITY_RENDER_LIMIT_INFINITE, limit));
	}

	public static boolean shouldLimitRenderedEntities() {
		return antiCrashEnabled && entityRenderLimit < ENTITY_RENDER_LIMIT_INFINITE;
	}

	public static int getParticleRenderLimit() {
		return particleRenderLimit;
	}

	public static void setParticleRenderLimit(int limit) {
		particleRenderLimit = Math.max(0, Math.min(PARTICLE_RENDER_LIMIT_INFINITE, limit));
	}

	public static boolean shouldLimitRenderedParticles() {
		return antiCrashEnabled && particleRenderLimit < PARTICLE_RENDER_LIMIT_INFINITE;
	}

	private static final class ConfigData {
		private Boolean antiCrashEnabled;
		private Boolean antiGuardianCrashEnabled;
		private Boolean blockElderGuardianEffect;
		private Boolean blockLoudNoisesEnabled;
		private Boolean hideLongNametagsEnabled;
		private Boolean interactionBypassEnabled;
		private Boolean ramPacketGuardEnabled;
		private Boolean structureOutlineGuardEnabled;
		private Integer entityRenderLimit;
		private Integer maxRenderedEntities;
		private Integer particleRenderLimit;
		private Integer maxRenderedParticles;

		private ConfigData() {
		}

		private ConfigData(
			boolean antiCrashEnabled,
			boolean antiGuardianCrashEnabled,
			boolean blockLoudNoisesEnabled,
			boolean hideLongNametagsEnabled,
			boolean interactionBypassEnabled,
			boolean ramPacketGuardEnabled,
			boolean structureOutlineGuardEnabled,
			int entityRenderLimit,
			int particleRenderLimit
		) {
			this.antiCrashEnabled = antiCrashEnabled;
			this.antiGuardianCrashEnabled = antiGuardianCrashEnabled;
			this.blockLoudNoisesEnabled = blockLoudNoisesEnabled;
			this.hideLongNametagsEnabled = hideLongNametagsEnabled;
			this.interactionBypassEnabled = interactionBypassEnabled;
			this.ramPacketGuardEnabled = ramPacketGuardEnabled;
			this.structureOutlineGuardEnabled = structureOutlineGuardEnabled;
			this.entityRenderLimit = entityRenderLimit;
			this.particleRenderLimit = particleRenderLimit;
		}
	}
}
