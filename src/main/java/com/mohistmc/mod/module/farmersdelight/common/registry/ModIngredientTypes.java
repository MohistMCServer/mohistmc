package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, FarmersDelight.MODID);

    public static final Supplier<IngredientType<?>> ITEM_ABILITY_INGREDIENT = INGREDIENT_TYPES.register("item_ability", () -> new IngredientType<>(ItemAbilityIngredient.CODEC));
}
