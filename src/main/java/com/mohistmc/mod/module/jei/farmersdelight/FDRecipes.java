package com.mohistmc.mod.module.jei.farmersdelight;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

// Reads Farmers Delight recipes directly from the client recipe manager (parsing the mod's own
// recipe JSONs). This is the reliable data source for custom recipe types like farmersdelight:cooking,
// which are not surfaced through JEI's server-synced RecipeMap.
public class FDRecipes
{
	private static final FileToIdConverter RECIPE_LISTER = FileToIdConverter.registry(Registries.RECIPE);
	private final ResourceManager resourceManager;
	private final Optional<HolderLookup.Provider> registries;

	public FDRecipes() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		this.resourceManager = minecraft.getResourceManager();
		this.registries = level != null ? Optional.of(level.registryAccess()) : Optional.empty();
	}

	public List<RecipeHolder<CookingPotRecipe>> getCookingPotRecipes() {
		return getRecipes(ModRecipeTypes.COOKING.get(), CookingPotRecipe.class);
	}

	public List<RecipeHolder<CuttingBoardRecipe>> getCuttingBoardRecipes() {
		return getRecipes(ModRecipeTypes.CUTTING.get(), CuttingBoardRecipe.class);
	}

	private <T extends Recipe<?>> List<RecipeHolder<T>> getRecipes(RecipeType<T> recipeType, Class<T> recipeClass) {
		if (this.registries.isEmpty()) {
			FarmersDelight.LOGGER.debug("Skipping JEI recipe population for {} before client registry access is available.", recipeType);
			return List.of();
		}

		List<RecipeHolder<T>> recipes = Lists.newArrayList();
		for (var entry : RECIPE_LISTER.listMatchingResourcesFromNamespace(this.resourceManager, FarmersDelight.MODID).entrySet()) {
			ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE, RECIPE_LISTER.fileToId(entry.getKey()));
			readRecipe(id, entry.getValue()).ifPresent(recipe -> {
				if (recipeClass.isInstance(recipe) && recipe.getType() == recipeType) {
					recipes.add(new RecipeHolder<>(id, recipeClass.cast(recipe)));
				}
			});
		}
		return recipes;
	}

	private Optional<Recipe<?>> readRecipe(ResourceKey<Recipe<?>> id, Resource resource) {
		try (var reader = resource.openAsReader()) {
			JsonElement json = JsonParser.parseReader(reader);
			return Recipe.CODEC.parse(this.registries.get().createSerializationContext(JsonOps.INSTANCE), json)
					.resultOrPartial(message -> FarmersDelight.LOGGER.debug("Skipping JEI recipe {}: {}", id.identifier(), message));
		} catch (IOException | RuntimeException ex) {
			FarmersDelight.LOGGER.debug("Skipping JEI recipe {} from {}.", id.identifier(), resource.sourcePackId());
			return Optional.empty();
		}
	}
}
