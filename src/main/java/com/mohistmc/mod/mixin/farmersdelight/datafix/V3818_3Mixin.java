package com.mohistmc.mod.mixin.farmersdelight.datafix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.SequencedMap;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V3818_3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(V3818_3.class)
public class V3818_3Mixin
{
	@Inject(method = "components", at = @At("RETURN"))
	private static void addFDCustomComponents(Schema schema, CallbackInfoReturnable<SequencedMap<String, Supplier<TypeTemplate>>> cir) {
		SequencedMap<String, Supplier<TypeTemplate>> components = cir.getReturnValue();
		components.put("mohistmc:meal", () -> References.ITEM_STACK.in(schema));
		components.put("mohistmc:container", () -> References.ITEM_STACK.in(schema));
	}
}
