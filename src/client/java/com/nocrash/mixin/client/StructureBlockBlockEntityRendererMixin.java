package com.nocrash.mixin.client;

import com.nocrash.config.NoCrashConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockBlockEntityRenderer.class)
public abstract class StructureBlockBlockEntityRendererMixin {
	@Unique
	private static final long NOCRASH$MAX_INVISIBLE_STRUCTURE_OUTLINES = 8_192L;

	@Inject(method = "updateStructureBoxRenderState", at = @At("HEAD"), cancellable = true)
	private static void nocrash$skipHugeStructureOutlines(BlockEntity blockEntity, StructureBlockBlockEntityRenderState state, CallbackInfo ci) {
		if (!NoCrashConfig.shouldGuardStructureOutlines()) {
			return;
		}

		if (!(blockEntity instanceof StructureBoxRendering structureBoxRendering)) {
			return;
		}

		if (structureBoxRendering.getRenderMode() != StructureBoxRendering.RenderMode.BOX_AND_INVISIBLE_BLOCKS) {
			return;
		}

		StructureBoxRendering.StructureBox structureBox = structureBoxRendering.getStructureBox();
		Vec3i size = structureBox.size();
		long x = Math.max(0, size.getX());
		long y = Math.max(0, size.getY());
		long z = Math.max(0, size.getZ());
		long volume = x * y * z;

		if (volume <= NOCRASH$MAX_INVISIBLE_STRUCTURE_OUTLINES) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		state.visible = player != null && (player.isCreativeLevelTwoOp() || player.isSpectator());
		state.structureBox = structureBox;
		state.renderMode = StructureBoxRendering.RenderMode.BOX;
		state.invisibleBlocks = null;
		state.field_62682 = null;
		ci.cancel();
	}
}
