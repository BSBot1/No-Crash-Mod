package com.nocrash.meteor.modules;

import com.nocrash.meteor.NoCrashMeteorAddon;
import com.nocrash.meteor.state.NoCrashState;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public final class AntiCrashModule extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgRenderLimits = settings.createGroup("Render Limits");

	public final Setting<Boolean> antiGuardianCrash = sgGeneral.add(new BoolSetting.Builder()
		.name("anti-guardian-crash")
		.description("Blocks elder guardian jumpscare packets, particles and curse sounds.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> blockLoudNoises = sgGeneral.add(new BoolSetting.Builder()
		.name("block-loud-noises")
		.description("Blocks loud sounds and world events used for audio spam.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> hideLongNametags = sgGeneral.add(new BoolSetting.Builder()
		.name("hide-long-nametags")
		.description("Hides suspiciously long or heavily formatted nametags.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> interactionBypass = sgGeneral.add(new BoolSetting.Builder()
		.name("interaction-bypass")
		.description("Lets you click through interaction entities.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> ramPacketGuard = sgGeneral.add(new BoolSetting.Builder()
		.name("ram-packet-guard")
		.description("Blocks oversized item packets to avoid RAM and packet crash exploits.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> structureOutlineGuard = sgGeneral.add(new BoolSetting.Builder()
		.name("structure-outline-guard")
		.description("Skips heavy structure invisible-block outlines if they are too large.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Integer> entityRenderLimit = sgRenderLimits.add(new IntSetting.Builder()
		.name("entity-render-limit")
		.description("Max entities rendered per frame. Set to 1001 for infinite.")
		.defaultValue(NoCrashState.ENTITY_RENDER_LIMIT_INFINITE)
		.range(0, NoCrashState.ENTITY_RENDER_LIMIT_INFINITE)
		.sliderRange(0, NoCrashState.ENTITY_RENDER_LIMIT_INFINITE)
		.build()
	);

	public final Setting<Integer> particleRenderLimit = sgRenderLimits.add(new IntSetting.Builder()
		.name("particle-render-limit")
		.description("Max particles rendered per frame. Set to 10001 for infinite.")
		.defaultValue(NoCrashState.PARTICLE_RENDER_LIMIT_INFINITE)
		.range(0, NoCrashState.PARTICLE_RENDER_LIMIT_INFINITE)
		.sliderRange(0, NoCrashState.PARTICLE_RENDER_LIMIT_INFINITE)
		.build()
	);

	public AntiCrashModule() {
		super(NoCrashMeteorAddon.CATEGORY, "anti-crash", "Client-side protections against crash and lag exploit spam.");
	}
}
