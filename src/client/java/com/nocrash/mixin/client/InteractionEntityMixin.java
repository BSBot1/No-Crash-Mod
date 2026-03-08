package com.nocrash.mixin.client;

import com.nocrash.config.NoCrashConfig;
import net.minecraft.entity.decoration.InteractionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionEntity.class)
public abstract class InteractionEntityMixin {
	@Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
	private void nocrash$interactionBypass(CallbackInfoReturnable<Boolean> cir) {
		if (NoCrashConfig.shouldBypassInteractionEntities()) {
			cir.setReturnValue(false);
		}
	}
}
