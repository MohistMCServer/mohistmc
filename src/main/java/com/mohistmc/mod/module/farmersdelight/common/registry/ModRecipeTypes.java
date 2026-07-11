package com.mohistmc.mod.module.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ModRecipeTypes
{
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, FarmersDelight.MODID);

	public static final Supplier<RecipeType<CookingPotRecipe>> COOKING = RECIPE_TYPES.register("cooking", () -> registerRecipeType("cooking"));
	public static final Supplier<RecipeType<CuttingBoardRecipe>> CUTTING = RECIPE_TYPES.register("cutting", () -> registerRecipeType("cutting"));

	public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
		return new RecipeType<>()
		{
			public String toString() {
				return FarmersDelight.MODID + ":" + identifier;
			}
		};
	}
}
