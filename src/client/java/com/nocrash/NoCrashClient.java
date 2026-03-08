package com.nocrash;

import com.nocrash.cloop.CommandLoopManager;
import com.nocrash.config.NoCrashConfig;
import com.nocrash.ui.NoCrashMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lwjgl.glfw.GLFW;

public final class NoCrashClient implements ClientModInitializer {
	public static final String MOD_ID = "nocrash";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
	private static final String OPEN_MENU_KEY_TRANSLATION = "key.nocrash.open_menu";
	private static final String TOGGLE_ANTI_CRASH_KEY_TRANSLATION = "key.nocrash.toggle_anti_crash";
	private static final String TOGGLE_ANTI_GUARDIAN_CRASH_KEY_TRANSLATION = "key.nocrash.toggle_anti_guardian_crash";
	private static final String TOGGLE_BLOCK_LOUD_NOISES_KEY_TRANSLATION = "key.nocrash.toggle_block_loud_noises";
	private static final String TOGGLE_HIDE_LONG_NAMETAGS_KEY_TRANSLATION = "key.nocrash.toggle_hide_long_nametags";
	private static final String TOGGLE_INTERACTION_BYPASS_KEY_TRANSLATION = "key.nocrash.toggle_interaction_bypass";
	private static final String TOGGLE_RAM_PACKET_GUARD_KEY_TRANSLATION = "key.nocrash.toggle_ram_packet_guard";
	private static final String TOGGLE_STRUCTURE_OUTLINE_GUARD_KEY_TRANSLATION = "key.nocrash.toggle_structure_outline_guard";

	private static KeyBinding openMenuKey;
	private static KeyBinding toggleAntiCrashKey;
	private static KeyBinding toggleAntiGuardianCrashKey;
	private static KeyBinding toggleBlockLoudNoisesKey;
	private static KeyBinding toggleHideLongNametagsKey;
	private static KeyBinding toggleInteractionBypassKey;
	private static KeyBinding toggleRamPacketGuardKey;
	private static KeyBinding toggleStructureOutlineGuardKey;

	@Override
	public void onInitializeClient() {
		NoCrashConfig.load();
		ClientSendMessageEvents.ALLOW_CHAT.register(message -> !CommandLoopManager.handleInput(MinecraftClient.getInstance(), message));

		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			OPEN_MENU_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_INSERT,
			KEY_CATEGORY
		));
		toggleAntiCrashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_ANTI_CRASH_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleAntiGuardianCrashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_ANTI_GUARDIAN_CRASH_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleBlockLoudNoisesKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_BLOCK_LOUD_NOISES_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleHideLongNametagsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_HIDE_LONG_NAMETAGS_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleInteractionBypassKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_INTERACTION_BYPASS_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleRamPacketGuardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_RAM_PACKET_GUARD_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));
		toggleStructureOutlineGuardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			TOGGLE_STRUCTURE_OUTLINE_GUARD_KEY_TRANSLATION,
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CommandLoopManager.tick(client);

			while (openMenuKey.wasPressed()) {
				if (!(client.currentScreen instanceof NoCrashMenuScreen)) {
					client.setScreen(new NoCrashMenuScreen(client.currentScreen));
				}
			}

			while (toggleAntiCrashKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isAntiCrashEnabled();
				NoCrashConfig.setAntiCrashEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.anti_crash", enabled);
			}

			while (toggleAntiGuardianCrashKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isAntiGuardianCrashEnabled();
				NoCrashConfig.setAntiGuardianCrashEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.anti_guardian_crash", enabled);
			}

			while (toggleBlockLoudNoisesKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isBlockLoudNoisesEnabled();
				NoCrashConfig.setBlockLoudNoisesEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.block_loud_noises", enabled);
			}

			while (toggleHideLongNametagsKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isHideLongNametagsEnabled();
				NoCrashConfig.setHideLongNametagsEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.hide_long_nametags", enabled);
			}

			while (toggleInteractionBypassKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isInteractionBypassEnabled();
				NoCrashConfig.setInteractionBypassEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.interaction_bypass", enabled);
			}

			while (toggleRamPacketGuardKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isRamPacketGuardEnabled();
				NoCrashConfig.setRamPacketGuardEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.ram_packet_guard", enabled);
			}

			while (toggleStructureOutlineGuardKey.wasPressed()) {
				boolean enabled = !NoCrashConfig.isStructureOutlineGuardEnabled();
				NoCrashConfig.setStructureOutlineGuardEnabled(enabled);
				NoCrashConfig.save();
				showToggleMessage(client, "screen.nocrash.structure_outline_guard", enabled);
			}
		});

		LOGGER.info("No Crash initialized");
	}

	private static void showToggleMessage(MinecraftClient client, String labelTranslationKey, boolean enabled) {
		if (client.inGameHud == null) {
			return;
		}

		client.inGameHud.setOverlayMessage(
			Text.translatable(
				"message.nocrash.toggled",
				Text.translatable(labelTranslationKey),
				Text.translatable(enabled ? "option.nocrash.on" : "option.nocrash.off")
			),
			false
		);
	}
}
