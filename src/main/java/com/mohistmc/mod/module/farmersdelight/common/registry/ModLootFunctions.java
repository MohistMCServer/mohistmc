package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.loot.function.CopySkilletFunction;
import com.mohistmc.mod.module.farmersdelight.common.loot.function.SmokerCookFunction;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootFunctions
{
	public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, FarmersDelight.MODID);

	public static final Supplier<MapCodec<CopySkilletFunction>> COPY_SKILLET = LOOT_FUNCTIONS.register("copy_skillet", () -> CopySkilletFunction.CODEC);
	public static final Supplier<MapCodec<SmokerCookFunction>> SMOKER_COOK = LOOT_FUNCTIONS.register("smoker_cook", () -> SmokerCookFunction.CODEC);
}
