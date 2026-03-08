package com.nocrash.meteor.mixin;

import com.nocrash.meteor.filter.LoudNoiseFilter;
import com.nocrash.meteor.render.ParticleRenderLimiter;
import com.nocrash.meteor.state.NoCrashState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
	@Inject(method = "addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianParticleSimple(
		ParticleEffect parameters,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelParticle(parameters)) {
			ci.cancel();
		}
	}

	@Inject(method = "addParticleClient(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianParticleDetailed(
		ParticleEffect parameters,
		boolean force,
		boolean canSpawnOnMinimal,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelParticle(parameters)) {
			ci.cancel();
		}
	}

	@Inject(method = "addImportantParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianImportantParticleSimple(
		ParticleEffect parameters,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelParticle(parameters)) {
			ci.cancel();
		}
	}

	@Inject(method = "addImportantParticleClient(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianImportantParticleDetailed(
		ParticleEffect parameters,
		boolean force,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelParticle(parameters)) {
			ci.cancel();
		}
	}

	@Inject(method = "playSoundClient(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianClientSound(
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelSound(sound)) {
			ci.cancel();
		}
	}

	@Inject(method = "playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianPositionedClientSound(
		double x,
		double y,
		double z,
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		boolean useDistance,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelSound(sound)) {
			ci.cancel();
		}
	}

	@Inject(method = "playSoundFromEntityClient(Lnet/minecraft/entity/Entity;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianEntityClientSound(
		Entity entity,
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelSound(sound)) {
			ci.cancel();
		}
	}

	@Inject(method = "playSound(Lnet/minecraft/entity/Entity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockNetworkSound(
		Entity source,
		double x,
		double y,
		double z,
		RegistryEntry<SoundEvent> sound,
		SoundCategory category,
		float volume,
		float pitch,
		long seed,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelSound(sound.value())) {
			ci.cancel();
		}
	}

	@Inject(method = "playSoundFromEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockNetworkEntitySound(
		Entity except,
		Entity source,
		RegistryEntry<SoundEvent> sound,
		SoundCategory category,
		float volume,
		float pitch,
		long seed,
		CallbackInfo ci
	) {
		if (nocrash$shouldCancelSound(sound.value())) {
			ci.cancel();
		}
	}

	private static boolean nocrash$shouldCancelParticle(ParticleEffect parameters) {
		if (NoCrashState.shouldBlockGuardianCrash() && parameters.getType() == ParticleTypes.ELDER_GUARDIAN) {
			return true;
		}

		return !ParticleRenderLimiter.shouldAllowParticle();
	}

	private static boolean nocrash$shouldCancelSound(SoundEvent sound) {
		if (NoCrashState.shouldBlockGuardianCrash() && sound.id().equals(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE.id())) {
			return true;
		}

		return NoCrashState.shouldBlockLoudNoises() && LoudNoiseFilter.shouldBlockSound(sound);
	}
}
