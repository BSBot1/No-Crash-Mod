package com.nocrash.mixin.client;

import com.nocrash.cloop.CommandLoopManager;
import com.nocrash.config.NoCrashConfig;
import com.nocrash.filter.LoudNoiseFilter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Unique
	private static final int NOCRASH$MAX_PARTICLES_PER_PACKET = 128;
	@Unique
	private static final int NOCRASH$MAX_PARTICLES_PER_SECOND = 500;
	@Unique
	private static final int NOCRASH$MAX_ITEM_BYTES = 65_536;
	@Unique
	private static final int NOCRASH$MAX_INVENTORY_PACKET_BYTES = 262_144;
	@Unique
	private static final int NOCRASH$MAX_INVENTORY_SLOT_COUNT = 512;
	@Unique
	private static final long NOCRASH$ALERT_COOLDOWN_MS = 1_500L;
	@Unique
	private static final long NOCRASH$AUTO_CLEAR_COOLDOWN_MS = 5_000L;

	@Unique
	private long nocrash$particleWindowStartMs;
	@Unique
	private int nocrash$particlesInWindow;
	@Unique
	private long nocrash$lastPacketAlertMs;
	@Unique
	private long nocrash$lastAutoClearMs;

	@Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianEffect(GameStateChangeS2CPacket packet, CallbackInfo ci) {
		if (packet.getReason() == GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT
			&& NoCrashConfig.shouldBlockGuardianCrash()) {
			ci.cancel();
		}
	}

	@Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
	private void nocrash$hideHiddenLoopGameMessages(GameMessageS2CPacket packet, CallbackInfo ci) {
		if (packet.overlay()) {
			return;
		}

		if (CommandLoopManager.shouldSuppressHiddenLoopOutput(packet.content())) {
			ci.cancel();
		}
	}

	@Inject(method = "onProfilelessChatMessage", at = @At("HEAD"), cancellable = true)
	private void nocrash$hideHiddenLoopProfilelessMessages(ProfilelessChatMessageS2CPacket packet, CallbackInfo ci) {
		if (CommandLoopManager.shouldSuppressHiddenLoopOutput(packet.message())) {
			ci.cancel();
		}
	}

	@Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
	private void nocrash$guardParticles(ParticleS2CPacket packet, CallbackInfo ci) {
		if (NoCrashConfig.shouldBlockGuardianCrash()
			&& packet.getParameters().getType() == ParticleTypes.ELDER_GUARDIAN) {
			ci.cancel();
			return;
		}

		if (!NoCrashConfig.isAntiCrashEnabled()) {
			return;
		}

		int packetCount = Math.max(packet.getCount(), 1);
		if (packetCount > NOCRASH$MAX_PARTICLES_PER_PACKET) {
			ci.cancel();
			return;
		}

		long now = Util.getMeasuringTimeMs();
		if (this.nocrash$particleWindowStartMs == 0L || now - this.nocrash$particleWindowStartMs >= 1_000L) {
			this.nocrash$particleWindowStartMs = now;
			this.nocrash$particlesInWindow = 0;
		}

		this.nocrash$particlesInWindow += packetCount;
		if (this.nocrash$particlesInWindow > NOCRASH$MAX_PARTICLES_PER_SECOND) {
			ci.cancel();
		}
	}

	@Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianSound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		if (nocrash$shouldCancelSound(packet.getSound().value())) {
			ci.cancel();
		}
	}

	@Inject(method = "onPlaySoundFromEntity", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockElderGuardianEntitySound(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {
		if (nocrash$shouldCancelSound(packet.getSound().value())) {
			ci.cancel();
		}
	}

	@Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
	private void nocrash$guardInventoryPacket(InventoryS2CPacket packet, CallbackInfo ci) {
		if (!NoCrashConfig.shouldGuardRamAndPackets()) {
			return;
		}

		if (packet.contents().size() > NOCRASH$MAX_INVENTORY_SLOT_COUNT) {
			nocrash$cancelDangerousItemPacket(ci, "inventory slots");
			return;
		}

		int totalBytes = 0;
		for (ItemStack stack : packet.contents()) {
			int size = nocrash$getEncodedItemBytes(stack, NOCRASH$MAX_ITEM_BYTES);
			if (size < 0) {
				nocrash$cancelDangerousItemPacket(ci, "inventory item");
				return;
			}

			totalBytes += size;
			if (totalBytes > NOCRASH$MAX_INVENTORY_PACKET_BYTES) {
				nocrash$cancelDangerousItemPacket(ci, "inventory packet size");
				return;
			}
		}

		int cursorSize = nocrash$getEncodedItemBytes(packet.cursorStack(), NOCRASH$MAX_ITEM_BYTES);
		if (cursorSize < 0 || totalBytes + cursorSize > NOCRASH$MAX_INVENTORY_PACKET_BYTES) {
			nocrash$cancelDangerousItemPacket(ci, "cursor item");
		}
	}

	@Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
	private void nocrash$guardSlotUpdatePacket(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
		if (!NoCrashConfig.shouldGuardRamAndPackets()) {
			return;
		}

		if (nocrash$getEncodedItemBytes(packet.getStack(), NOCRASH$MAX_ITEM_BYTES) < 0) {
			nocrash$cancelDangerousItemPacket(ci, "slot item");
		}
	}

	@Inject(method = "onSetCursorItem", at = @At("HEAD"), cancellable = true)
	private void nocrash$guardCursorItemPacket(SetCursorItemS2CPacket packet, CallbackInfo ci) {
		if (!NoCrashConfig.shouldGuardRamAndPackets()) {
			return;
		}

		if (nocrash$getEncodedItemBytes(packet.contents(), NOCRASH$MAX_ITEM_BYTES) < 0) {
			nocrash$cancelDangerousItemPacket(ci, "cursor item");
		}
	}

	@Inject(method = "onSetPlayerInventory", at = @At("HEAD"), cancellable = true)
	private void nocrash$guardSetPlayerInventoryPacket(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
		if (!NoCrashConfig.shouldGuardRamAndPackets()) {
			return;
		}

		if (nocrash$getEncodedItemBytes(packet.contents(), NOCRASH$MAX_ITEM_BYTES) < 0) {
			nocrash$cancelDangerousItemPacket(ci, "inventory item");
		}
	}

	@Inject(method = "onWorldEvent", at = @At("HEAD"), cancellable = true)
	private void nocrash$blockLoudWorldEvents(WorldEventS2CPacket packet, CallbackInfo ci) {
		if (NoCrashConfig.shouldBlockLoudNoises() && LoudNoiseFilter.shouldBlockWorldEvent(packet.getEventId())) {
			ci.cancel();
		}
	}

	@Unique
	private static boolean nocrash$shouldCancelSound(SoundEvent sound) {
		if (NoCrashConfig.shouldBlockGuardianCrash() && sound.id().equals(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE.id())) {
			return true;
		}

		return NoCrashConfig.shouldBlockLoudNoises() && LoudNoiseFilter.shouldBlockSound(sound);
	}

	@Unique
	private int nocrash$getEncodedItemBytes(ItemStack stack, int limit) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}

		ByteBuf raw = Unpooled.buffer(0, limit);
		try {
			RegistryByteBuf registryBuf = new RegistryByteBuf(raw, ((ClientPlayNetworkHandler) (Object) this).getRegistryManager());
			ItemStack.PACKET_CODEC.encode(registryBuf, stack);
			return raw.writerIndex();
		} catch (Throwable throwable) {
			return -1;
		} finally {
			raw.release();
		}
	}

	@Unique
	private void nocrash$cancelDangerousItemPacket(CallbackInfo ci, String source) {
		ci.cancel();
		nocrash$showBlockedPacketAlert(source);
		nocrash$attemptAutoClear();
	}

	@Unique
	private void nocrash$showBlockedPacketAlert(String source) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.inGameHud == null) {
			return;
		}

		long now = Util.getMeasuringTimeMs();
		if (now - this.nocrash$lastPacketAlertMs < NOCRASH$ALERT_COOLDOWN_MS) {
			return;
		}

		this.nocrash$lastPacketAlertMs = now;
		client.inGameHud.setOverlayMessage(
			Text.translatable("message.nocrash.ram_packet_blocked", Text.literal(source)),
			false
		);
	}

	@Unique
	private void nocrash$attemptAutoClear() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
			return;
		}

		if (client.player != null) {
			client.player.closeHandledScreen();
		}

		long now = Util.getMeasuringTimeMs();
		if (now - this.nocrash$lastAutoClearMs < NOCRASH$AUTO_CLEAR_COOLDOWN_MS) {
			return;
		}

		ClientPlayNetworkHandler handler = client.getNetworkHandler();
		if (handler == null || handler.getCommandDispatcher() == null || handler.getCommandDispatcher().getRoot().getChild("clear") == null) {
			return;
		}

		this.nocrash$lastAutoClearMs = now;
		handler.sendChatCommand("clear @s");
		if (client.inGameHud != null) {
			client.inGameHud.setOverlayMessage(Text.translatable("message.nocrash.ram_packet_clear_attempted"), false);
		}
	}
}
