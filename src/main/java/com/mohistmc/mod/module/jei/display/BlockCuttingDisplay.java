package com.mohistmc.mod.module.jei.display;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public record BlockCuttingDisplay(Identifier id, Ingredient input, List<List<ItemStackTemplate>> outputs) {
}
