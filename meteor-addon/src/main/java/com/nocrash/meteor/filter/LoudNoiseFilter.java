package com.nocrash.meteor.filter;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldEvents;

import java.util.Set;

public final class LoudNoiseFilter {
	private static final Set<Identifier> BLOCKED_SOUND_IDS = Set.of(
		SoundEvents.ENTITY_ENDER_DRAGON_DEATH.id(),
		SoundEvents.ENTITY_ENDER_DRAGON_GROWL.id(),
		SoundEvents.ENTITY_WITHER_SPAWN.id(),
		SoundEvents.ENTITY_WITHER_DEATH.id(),
		SoundEvents.BLOCK_END_PORTAL_SPAWN.id(),
		SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER.id(),
		SoundEvents.ENTITY_WARDEN_SONIC_BOOM.id(),
		SoundEvents.ENTITY_WARDEN_ROAR.id(),
		SoundEvents.EVENT_RAID_HORN.value().id()
	);

	private static final Set<Integer> BLOCKED_WORLD_EVENT_IDS = Set.of(
		WorldEvents.ENDER_DRAGON_DIES,
		WorldEvents.WITHER_SPAWNS,
		WorldEvents.END_PORTAL_OPENED,
		WorldEvents.END_GATEWAY_SPAWNS,
		WorldEvents.ENDER_DRAGON_RESURRECTED,
		WorldEvents.WITHER_BREAKS_BLOCK
	);

	private LoudNoiseFilter() {
	}

	public static boolean shouldBlockSound(SoundEvent sound) {
		return BLOCKED_SOUND_IDS.contains(sound.id());
	}

	public static boolean shouldBlockWorldEvent(int worldEventId) {
		return BLOCKED_WORLD_EVENT_IDS.contains(worldEventId);
	}
}
