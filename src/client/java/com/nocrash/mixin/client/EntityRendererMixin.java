package com.nocrash.mixin.client;

import com.nocrash.config.NoCrashConfig;
import com.nocrash.filter.NametagFilter;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
	@Shadow
	protected abstract Text getDisplayName(T entity);

	@Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
	private void nocrash$hideLongNametags(T entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
		if (!NoCrashConfig.shouldHideLongNametags()) {
			return;
		}

		if (NametagFilter.shouldHide(this.getDisplayName(entity))) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	private void nocrash$hideLongNametagsAtRender(S state, net.minecraft.client.util.math.MatrixStack matrices,
		net.minecraft.client.render.command.OrderedRenderCommandQueue queue,
		net.minecraft.client.render.state.CameraRenderState cameraState, CallbackInfo ci) {
		if (!NoCrashConfig.shouldHideLongNametags()) {
			return;
		}

		Text displayName = state.displayName;
		if (displayName != null && NametagFilter.shouldHide(displayName)) {
			ci.cancel();
		}
	}
}
