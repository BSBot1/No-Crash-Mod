package com.nocrash.meteor.mixin;

import com.nocrash.meteor.state.NoCrashState;
import net.minecraft.entity.decoration.InteractionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionEntity.class)
public abstract class InteractionEntityMixin {
	@Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
	private void nocrash$interactionBypass(CallbackInfoReturnable<Boolean> cir) {
		if (NoCrashState.shouldBypassInteractionEntities()) {
			cir.setReturnValue(false);
		}
	}
}
