package com.mohistmc.mod.module.farmersdelight.common.block.entity.container;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

@ParametersAreNonnullByDefault
public class CookingPotMealSlot extends ResourceHandlerSlot
{
	public CookingPotMealSlot(ItemStacksResourceHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, inventoryIn::set, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return false;
	}
}
