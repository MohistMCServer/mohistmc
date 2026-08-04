package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.DoughRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.FoodServingRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FarmersDelight.MODID);

	public static final Supplier<RecipeSerializer<CookingPotRecipe>> COOKING = RECIPE_SERIALIZERS.register("cooking",
			() -> new RecipeSerializer<>(CookingPotRecipe.Serializer.CODEC, CookingPotRecipe.Serializer.STREAM_CODEC));
	public static final Supplier<RecipeSerializer<CuttingBoardRecipe>> CUTTING = RECIPE_SERIALIZERS.register("cutting",
			() -> new RecipeSerializer<>(CuttingBoardRecipe.Serializer.CODEC, CuttingBoardRecipe.Serializer.STREAM_CODEC));

	public static final Supplier<RecipeSerializer<FoodServingRecipe>> FOOD_SERVING =
			RECIPE_SERIALIZERS.register("food_serving", () -> new RecipeSerializer<>(FoodServingRecipe.CODEC, FoodServingRecipe.STREAM_CODEC));
	public static final Supplier<RecipeSerializer<DoughRecipe>> DOUGH =
			RECIPE_SERIALIZERS.register("dough", () -> new RecipeSerializer<>(DoughRecipe.CODEC, DoughRecipe.STREAM_CODEC));
}
