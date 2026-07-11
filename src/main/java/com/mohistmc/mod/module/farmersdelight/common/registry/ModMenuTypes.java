package com.mohistmc.mod.module.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.container.CookingPotMenu;

public class ModMenuTypes
{
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, FarmersDelight.MODID);

	public static final Supplier<MenuType<CookingPotMenu>> COOKING_POT = MENU_TYPES
			.register("cooking_pot", () -> IMenuTypeExtension.create(CookingPotMenu::new));
}
