package com.mohistmc.mod.module.jei.farmersdelight;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.jei.farmersdelight.resource.DecompositionDummy;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class FDRecipeTypes
{
	public static final RecipeType<RecipeHolder<CookingPotRecipe>> COOKING = RecipeType.createFromVanilla(ModRecipeTypes.COOKING.get());
	public static final RecipeType<RecipeHolder<CuttingBoardRecipe>> CUTTING = RecipeType.createFromVanilla(ModRecipeTypes.CUTTING.get());
	public static final RecipeType<DecompositionDummy> DECOMPOSITION = RecipeType.create(FarmersDelight.MODID, "decomposition", DecompositionDummy.class);
}
