package com.mohistmc.mod.mixin.farmersdelight;

import com.mohistmc.mod.module.farmersdelight.common.registry.ModEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class NourishmentAlwaysEatMixin
{
	@Inject(
			method = "canEat",
			at = @At("HEAD"),
			cancellable = true)
	private void alwaysEatUnderNourishmentEffect(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
		if (((Player) (Object) this).hasEffect(ModEffects.NOURISHMENT)) {
			cir.setReturnValue(true);
		}
	}
}
