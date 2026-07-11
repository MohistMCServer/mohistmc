package com.mohistmc.mod.module.farmersdelight.common.mixin.datafix;

import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin
{
	@Inject(method = "fixItemStack", at = @At("HEAD"))
	private static void fixCustomStacks(ItemStackComponentizationFix.ItemStackData itemStack, Dynamic<?> dynamic, CallbackInfo ci) {
		if (itemStack.is("mohistmc:cooking_pot")) {
			itemStack.fixSubTag("BlockEntityTag", false, subTag -> {
				Optional<? extends Dynamic<?>> container = subTag.get("Container").result();
				container.ifPresent(_ -> {
					Dynamic<?> result = dynamic.set("count", dynamic.createInt(dynamic.get("Count").asInt(1))).remove("Count");
					itemStack.setComponent("mohistmc:container", result);
				});

				Optional<? extends Dynamic<?>> inventory = subTag.get("Inventory").result();
				if (inventory.isPresent()) {
					List<Dynamic<?>> list = inventory.get().get("Items").asList(_ ->
							dynamic.set("count", dynamic.createInt(dynamic.get("Count").asInt(2))).remove("Count").remove("Slot"));
					if (!list.isEmpty()) {
						itemStack.setComponent("mohistmc:meal", list.getFirst());
					}
				}

				return subTag.remove("Container").remove("Inventory");
			});
		}
	}
}