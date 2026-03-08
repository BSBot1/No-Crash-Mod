package com.nocrash.mixin.client;

import com.nocrash.render.EntityRenderLimiter;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderManager.class)
public abstract class EntityRenderManagerMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private <S extends EntityRenderState> void nocrash$limitEntityRendering(
		S state,
		CameraRenderState cameraState,
		double x,
		double y,
		double z,
		MatrixStack matrices,
		OrderedRenderCommandQueue queue,
		CallbackInfo ci
	) {
		if (!EntityRenderLimiter.shouldRenderNextEntity()) {
			ci.cancel();
		}
	}
}
