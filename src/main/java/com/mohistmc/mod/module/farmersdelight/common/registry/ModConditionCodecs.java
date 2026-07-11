package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.condition.VanillaCrateEnabledCondition;

@SuppressWarnings("unused")
public class ModConditionCodecs
{
	public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, FarmersDelight.MODID);

	public static final Supplier<MapCodec<? extends ICondition>> VANILLA_CRATE_ENABLED = CONDITION_CODECS.register("vanilla_crates_enabled", () -> VanillaCrateEnabledCondition.CODEC);
}
