package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.common.utility.RecipeUtils;

public class RegistryAliases
{
	public static void addRegistryAliases() {
		addBlockAlias("basket", "bamboo_basket");
		addItemAlias("basket", "bamboo_basket");
	}

	public static void addBlockAlias(String oldName, String newName) {
		ModBlocks.BLOCKS.addAlias(RecipeUtils.FDLocation(oldName), RecipeUtils.FDLocation(newName));
	}

	public static void addItemAlias(String oldName, String newName) {
		ModItems.ITEMS.addAlias(RecipeUtils.FDLocation(oldName), RecipeUtils.FDLocation(newName));
	}
}
