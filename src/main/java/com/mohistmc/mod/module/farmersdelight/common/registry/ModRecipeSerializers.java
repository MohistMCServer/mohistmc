package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import com.mohistmc.mod.module.farmersdelight.common.crafting.DoughRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.FoodServingRecipe;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers
{
	// RecipeSerializer is a vanilla built-in registry: register against BuiltInRegistries
	// (same as Create's AllRecipeSerializers) for consistent registry identity.
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, FarmersDelight.MODID);

	public static final Supplier<RecipeSerializer<? extends Recipe<RecipeInput>>> COOKING = RECIPE_SERIALIZERS.register("cooking", fromCodecs(CookingPotRecipe.CODEC, CookingPotRecipe.STREAM_CODEC));
	public static final Supplier<RecipeSerializer<? extends Recipe<CuttingBoardRecipeInput>>> CUTTING = RECIPE_SERIALIZERS.register("cutting", fromCodecs(CuttingBoardRecipe.CODEC, CuttingBoardRecipe.STREAM_CODEC));

	public static final Supplier<RecipeSerializer<?>> FOOD_SERVING =
			RECIPE_SERIALIZERS.register("food_serving", () -> FoodServingRecipe.SERIALIZER);
	public static final Supplier<RecipeSerializer<?>> DOUGH =
			RECIPE_SERIALIZERS.register("dough", () -> DoughRecipe.SERIALIZER);

	private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> fromCodecs(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
		return () -> new RecipeSerializer<T>(codec, streamCodec);
	}
}
