package com.nocrash.meteor.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.nocrash.meteor.render.EntityRenderLimiter;
import com.nocrash.meteor.render.ParticleRenderLimiter;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void nocrash$resetEntityRenderCounter(
		ObjectAllocator allocator,
		RenderTickCounter tickCounter,
		boolean renderBlockOutline,
		Camera camera,
		Matrix4f positionMatrix,
		Matrix4f projectionMatrix,
		Matrix4f frustumMatrix,
		GpuBufferSlice fogBuffer,
		Vector4f clearColor,
		boolean shouldRenderTranslucent,
		CallbackInfo ci
	) {
		EntityRenderLimiter.beginFrame();
		ParticleRenderLimiter.beginFrame();
	}
}
