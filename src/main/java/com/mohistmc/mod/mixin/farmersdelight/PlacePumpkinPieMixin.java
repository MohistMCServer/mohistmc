package com.mohistmc.mod.mixin.farmersdelight;

import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PlacePumpkinPieMixin
{
	@Inject(
			method = "useOn",
			at = @At("TAIL"),
			cancellable = true)
	private void usePumpkinPie(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		if (!context.getItemInHand().is(Items.PUMPKIN_PIE))
			return;

		cir.setReturnValue(ModItems.DEBUG_PUMPKIN_PIE.get().useOn(context));
	}
}
