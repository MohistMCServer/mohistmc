package com.mohistmc.mod.module.farmersdelight.common.utility;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import net.minecraft.resources.Identifier;

public class RecipeUtils
{
	public static Identifier FDLocation(String name) {
		return Identifier.fromNamespaceAndPath(FarmersDelight.MODID, name);
	}
}
