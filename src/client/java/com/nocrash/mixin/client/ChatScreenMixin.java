package com.nocrash.mixin.client;

import com.nocrash.cloop.CommandLoopManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
	@Shadow
	@Final
	protected TextRenderer textRenderer;
	@Shadow
	protected TextFieldWidget chatField;

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void nocrash$tabCompleteDotCommands(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (keyCode != GLFW.GLFW_KEY_TAB) {
			return;
		}

		String completed = CommandLoopManager.completeTab(this.chatField.getText());
		if (completed == null) {
			return;
		}

		this.chatField.setText(completed);
		this.chatField.setCursorToEnd(false);
		cir.setReturnValue(true);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void nocrash$renderDotCommandSuggestions(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		List<String> suggestions = CommandLoopManager.getLiveSuggestions(this.chatField.getText(), 6);
		if (suggestions.isEmpty()) {
			return;
		}

		int lineHeight = 12;
		int padding = 4;
		int maxWidth = 0;
		for (String suggestion : suggestions) {
			maxWidth = Math.max(maxWidth, this.textRenderer.getWidth(suggestion));
		}

		int boxLeft = 4;
		int boxRight = boxLeft + maxWidth + (padding * 2);
		int boxBottom = this.chatField.getY() - 2;
		int boxTop = boxBottom - (suggestions.size() * lineHeight) - 2;

		context.fill(boxLeft, boxTop, boxRight, boxBottom, 0xAA000000);

		for (int i = 0; i < suggestions.size(); i++) {
			int color = i == 0 ? 0xFFFFFF : 0xB0B0B0;
			int textY = boxBottom - ((i + 1) * lineHeight) + 2;
			context.drawTextWithShadow(this.textRenderer, Text.literal(suggestions.get(i)), boxLeft + padding, textY, color);
		}
	}
}
