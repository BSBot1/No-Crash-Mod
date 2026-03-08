package com.nocrash.mixin.client;

import com.nocrash.ui.NoCrashMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void nocrash$addOpenMenuButton(CallbackInfo ci) {
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.nocrash.open_menu_button"), button -> {
			if (this.client != null) {
				this.client.setScreen(new NoCrashMenuScreen(this));
			}
		}).dimensions(this.width - 124, 4, 120, 20).build());
	}
}
