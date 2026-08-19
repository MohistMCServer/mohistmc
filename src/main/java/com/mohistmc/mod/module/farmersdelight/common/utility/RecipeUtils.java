package com.mohistmc.mod.module.farmersdelight.common.utility;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeUtils
{
	public static Identifier FDLocation(String name) {
		return Identifier.fromNamespaceAndPath(FarmersDelight.MODID, name);
	}
}
