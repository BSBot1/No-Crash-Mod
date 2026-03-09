package com.nocrash.ui;

import com.nocrash.config.NoCrashConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public final class NoCrashMenuScreen extends Screen {
	private final Screen parent;
	private ButtonWidget antiCrashButton;
	private ButtonWidget elderGuardianButton;
	private ButtonWidget loudNoisesButton;
	private ButtonWidget hideLongNametagsButton;
	private ButtonWidget interactionBypassButton;
	private ButtonWidget ramPacketGuardButton;
	private ButtonWidget structureOutlineGuardButton;
	private EntityLimitSlider entityRenderLimitSlider;
	private ParticleLimitSlider particleRenderLimitSlider;
	private BlockEntityLimitSlider blockEntityRenderLimitSlider;

	public NoCrashMenuScreen(Screen parent) {
		super(Text.translatable("screen.nocrash.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int topY = this.height / 4;

		this.antiCrashButton = this.addDrawableChild(ButtonWidget.builder(getAntiCrashLabel(), button -> {
			NoCrashConfig.setAntiCrashEnabled(!NoCrashConfig.isAntiCrashEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY, 220, 20).build());

		this.elderGuardianButton = this.addDrawableChild(ButtonWidget.builder(getElderGuardianLabel(), button -> {
			NoCrashConfig.setAntiGuardianCrashEnabled(!NoCrashConfig.isAntiGuardianCrashEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 24, 220, 20).build());

		this.loudNoisesButton = this.addDrawableChild(ButtonWidget.builder(getLoudNoisesLabel(), button -> {
			NoCrashConfig.setBlockLoudNoisesEnabled(!NoCrashConfig.isBlockLoudNoisesEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 48, 220, 20).build());

		this.hideLongNametagsButton = this.addDrawableChild(ButtonWidget.builder(getHideLongNametagsLabel(), button -> {
			NoCrashConfig.setHideLongNametagsEnabled(!NoCrashConfig.isHideLongNametagsEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 72, 220, 20).build());

		this.interactionBypassButton = this.addDrawableChild(ButtonWidget.builder(getInteractionBypassLabel(), button -> {
			NoCrashConfig.setInteractionBypassEnabled(!NoCrashConfig.isInteractionBypassEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 96, 220, 20).build());

		this.ramPacketGuardButton = this.addDrawableChild(ButtonWidget.builder(getRamPacketGuardLabel(), button -> {
			NoCrashConfig.setRamPacketGuardEnabled(!NoCrashConfig.isRamPacketGuardEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 120, 220, 20).build());

		this.structureOutlineGuardButton = this.addDrawableChild(ButtonWidget.builder(getStructureOutlineGuardLabel(), button -> {
			NoCrashConfig.setStructureOutlineGuardEnabled(!NoCrashConfig.isStructureOutlineGuardEnabled());
			NoCrashConfig.save();
			refreshButtons();
		}).dimensions(centerX - 110, topY + 144, 220, 20).build());

		this.entityRenderLimitSlider = this.addDrawableChild(new EntityLimitSlider(centerX - 110, topY + 168, 220, 20));
		this.particleRenderLimitSlider = this.addDrawableChild(new ParticleLimitSlider(centerX - 110, topY + 192, 220, 20));
		this.blockEntityRenderLimitSlider = this.addDrawableChild(new BlockEntityLimitSlider(centerX - 110, topY + 216, 220, 20));

		this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
			.dimensions(centerX - 110, topY + 244, 220, 20)
			.build());

		refreshButtons();
	}

	private void refreshButtons() {
		this.antiCrashButton.setMessage(getAntiCrashLabel());
		this.elderGuardianButton.setMessage(getElderGuardianLabel());
		this.loudNoisesButton.setMessage(getLoudNoisesLabel());
		this.hideLongNametagsButton.setMessage(getHideLongNametagsLabel());
		this.interactionBypassButton.setMessage(getInteractionBypassLabel());
		this.ramPacketGuardButton.setMessage(getRamPacketGuardLabel());
		this.structureOutlineGuardButton.setMessage(getStructureOutlineGuardLabel());
		this.entityRenderLimitSlider.syncFromConfig();
		this.particleRenderLimitSlider.syncFromConfig();
		this.blockEntityRenderLimitSlider.syncFromConfig();
		this.elderGuardianButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.loudNoisesButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.hideLongNametagsButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.interactionBypassButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.ramPacketGuardButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.structureOutlineGuardButton.active = NoCrashConfig.isAntiCrashEnabled();
		this.entityRenderLimitSlider.active = NoCrashConfig.isAntiCrashEnabled();
		this.particleRenderLimitSlider.active = NoCrashConfig.isAntiCrashEnabled();
		this.blockEntityRenderLimitSlider.active = NoCrashConfig.isAntiCrashEnabled();
	}

	private Text getAntiCrashLabel() {
		return Text.translatable("screen.nocrash.anti_crash", getOnOff(NoCrashConfig.isAntiCrashEnabled()));
	}

	private Text getElderGuardianLabel() {
		return Text.translatable("screen.nocrash.anti_guardian_crash", getOnOff(NoCrashConfig.isAntiGuardianCrashEnabled()));
	}

	private Text getLoudNoisesLabel() {
		return Text.translatable("screen.nocrash.block_loud_noises", getOnOff(NoCrashConfig.isBlockLoudNoisesEnabled()));
	}

	private Text getHideLongNametagsLabel() {
		return Text.translatable("screen.nocrash.hide_long_nametags", getOnOff(NoCrashConfig.isHideLongNametagsEnabled()));
	}

	private Text getInteractionBypassLabel() {
		return Text.translatable("screen.nocrash.interaction_bypass", getOnOff(NoCrashConfig.isInteractionBypassEnabled()));
	}

	private Text getRamPacketGuardLabel() {
		return Text.translatable("screen.nocrash.ram_packet_guard", getOnOff(NoCrashConfig.isRamPacketGuardEnabled()));
	}

	private Text getStructureOutlineGuardLabel() {
		return Text.translatable("screen.nocrash.structure_outline_guard", getOnOff(NoCrashConfig.isStructureOutlineGuardEnabled()));
	}

	private Text getEntityRenderLimitLabel() {
		return Text.translatable("screen.nocrash.entity_render_limit", getEntityRenderLimitValueLabel());
	}

	private Text getEntityRenderLimitValueLabel() {
		int limit = NoCrashConfig.getEntityRenderLimit();
		if (limit >= NoCrashConfig.ENTITY_RENDER_LIMIT_INFINITE) {
			return Text.translatable("option.nocrash.infinite");
		}
		return Text.literal(Integer.toString(limit));
	}

	private Text getParticleRenderLimitLabel() {
		return Text.translatable("screen.nocrash.particle_render_limit", getParticleRenderLimitValueLabel());
	}

	private Text getParticleRenderLimitValueLabel() {
		int limit = NoCrashConfig.getParticleRenderLimit();
		if (limit >= NoCrashConfig.PARTICLE_RENDER_LIMIT_INFINITE) {
			return Text.translatable("option.nocrash.infinite");
		}
		return Text.literal(Integer.toString(limit));
	}

	private Text getBlockEntityRenderLimitLabel() {
		return Text.translatable("screen.nocrash.block_entity_render_limit", getBlockEntityRenderLimitValueLabel());
	}

	private Text getBlockEntityRenderLimitValueLabel() {
		int limit = NoCrashConfig.getBlockEntityRenderLimit();
		if (limit >= NoCrashConfig.BLOCK_ENTITY_RENDER_LIMIT_INFINITE) {
			return Text.translatable("option.nocrash.infinite");
		}
		return Text.literal(Integer.toString(limit));
	}

	private Text getOnOff(boolean enabled) {
		return Text.translatable(enabled ? "option.nocrash.on" : "option.nocrash.off");
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer,
			Text.translatable("screen.nocrash.hint"),
			this.width / 2,
			this.height / 4 + 286,
			0xA0A0A0
		);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	private final class EntityLimitSlider extends SliderWidget {
		private EntityLimitSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(), toSliderValue(NoCrashConfig.getEntityRenderLimit()));
			this.updateMessage();
		}

		private static double toSliderValue(int limit) {
			int clamped = Math.max(0, Math.min(NoCrashConfig.ENTITY_RENDER_LIMIT_INFINITE, limit));
			return clamped / (double) NoCrashConfig.ENTITY_RENDER_LIMIT_INFINITE;
		}

		private static int toLimit(double sliderValue) {
			double clamped = Math.max(0.0D, Math.min(1.0D, sliderValue));
			return (int) Math.round(clamped * NoCrashConfig.ENTITY_RENDER_LIMIT_INFINITE);
		}

		@Override
		protected void updateMessage() {
			this.setMessage(getEntityRenderLimitLabel());
		}

		@Override
		protected void applyValue() {
			int selectedLimit = toLimit(this.value);
			if (selectedLimit != NoCrashConfig.getEntityRenderLimit()) {
				NoCrashConfig.setEntityRenderLimit(selectedLimit);
				NoCrashConfig.save();
			}
			this.updateMessage();
		}

		private void syncFromConfig() {
			this.value = toSliderValue(NoCrashConfig.getEntityRenderLimit());
			this.updateMessage();
		}
	}

	private final class ParticleLimitSlider extends SliderWidget {
		private ParticleLimitSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(), toSliderValue(NoCrashConfig.getParticleRenderLimit()));
			this.updateMessage();
		}

		private static double toSliderValue(int limit) {
			int clamped = Math.max(0, Math.min(NoCrashConfig.PARTICLE_RENDER_LIMIT_INFINITE, limit));
			return clamped / (double) NoCrashConfig.PARTICLE_RENDER_LIMIT_INFINITE;
		}

		private static int toLimit(double sliderValue) {
			double clamped = Math.max(0.0D, Math.min(1.0D, sliderValue));
			return (int) Math.round(clamped * NoCrashConfig.PARTICLE_RENDER_LIMIT_INFINITE);
		}

		@Override
		protected void updateMessage() {
			this.setMessage(getParticleRenderLimitLabel());
		}

		@Override
		protected void applyValue() {
			int selectedLimit = toLimit(this.value);
			if (selectedLimit != NoCrashConfig.getParticleRenderLimit()) {
				NoCrashConfig.setParticleRenderLimit(selectedLimit);
				NoCrashConfig.save();
			}
			this.updateMessage();
		}

		private void syncFromConfig() {
			this.value = toSliderValue(NoCrashConfig.getParticleRenderLimit());
			this.updateMessage();
		}
	}

	private final class BlockEntityLimitSlider extends SliderWidget {
		private BlockEntityLimitSlider(int x, int y, int width, int height) {
			super(x, y, width, height, Text.empty(), toSliderValue(NoCrashConfig.getBlockEntityRenderLimit()));
			this.updateMessage();
		}

		private static double toSliderValue(int limit) {
			int clamped = Math.max(0, Math.min(NoCrashConfig.BLOCK_ENTITY_RENDER_LIMIT_INFINITE, limit));
			return clamped / (double) NoCrashConfig.BLOCK_ENTITY_RENDER_LIMIT_INFINITE;
		}

		private static int toLimit(double sliderValue) {
			double clamped = Math.max(0.0D, Math.min(1.0D, sliderValue));
			return (int) Math.round(clamped * NoCrashConfig.BLOCK_ENTITY_RENDER_LIMIT_INFINITE);
		}

		@Override
		protected void updateMessage() {
			this.setMessage(getBlockEntityRenderLimitLabel());
		}

		@Override
		protected void applyValue() {
			int selectedLimit = toLimit(this.value);
			if (selectedLimit != NoCrashConfig.getBlockEntityRenderLimit()) {
				NoCrashConfig.setBlockEntityRenderLimit(selectedLimit);
				NoCrashConfig.save();
			}
			this.updateMessage();
		}

		private void syncFromConfig() {
			this.value = toSliderValue(NoCrashConfig.getBlockEntityRenderLimit());
			this.updateMessage();
		}
	}
}
