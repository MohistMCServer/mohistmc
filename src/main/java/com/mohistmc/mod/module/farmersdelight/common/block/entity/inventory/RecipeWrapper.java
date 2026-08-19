package com.mohistmc.mod.module.farmersdelight.common.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Wrapper for ItemStacksResourceHandler.
 */
public class RecipeWrapper implements RecipeInput {

	private final ItemStacksResourceHandler handler;

	public RecipeWrapper(ItemStacksResourceHandler handler) {
		this.handler = handler;
	}

	@Override
	public ItemStack getItem(int slot) {
		return handler.getResource(slot).toStack();
	}

	@Override
	public int size() {
		return handler.size();
	}
}