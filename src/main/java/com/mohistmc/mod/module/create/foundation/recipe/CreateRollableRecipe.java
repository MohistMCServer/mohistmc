package com.mohistmc.mod.module.create.foundation.recipe;

import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public interface CreateRollableRecipe<T extends RecipeInput> extends CreateRecipe<T> {
    @Override
    default ItemStack assemble(T input) {
        return ItemStack.EMPTY;
    }

    List<ItemStack> assemble(T input, RandomSource random);
}
