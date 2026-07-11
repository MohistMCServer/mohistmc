package com.mohistmc.mod.module.farmersdelight.common.block.entity.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class CookingPotItemHandler implements ResourceHandler<ItemResource>
{
	private static final int SLOTS_INPUT = 6;
	private static final int SLOT_CONTAINER_INPUT = 7;
	private static final int SLOT_MEAL_OUTPUT = 8;
	private final ItemStacksResourceHandler handler;
	private final Direction side;

	public CookingPotItemHandler(ItemStacksResourceHandler handler, @Nullable Direction side) {
		this.handler = handler;
		this.side = side;
	}

	@Override
	public int size() {
		return handler.size();
	}

	@Override
	public ItemResource getResource(int slot) {
		return handler.getResource(slot);
	}

	@Override
	public long getAmountAsLong(int slot) {
		return handler.getAmountAsLong(slot);
	}

	@Override
	public long getCapacityAsLong(int slot, ItemResource resource) {
		return handler.getCapacityAsLong(slot, resource);
	}

	@Override
	public boolean isValid(int slot, ItemResource resource) {
		return handler.isValid(slot, resource);
	}

	@Override
	public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
		if (side == null || side.equals(Direction.UP)) {
			return slot < SLOTS_INPUT ? handler.insert(slot, resource, amount, transaction) : 0;
		} else {
			return slot == SLOT_CONTAINER_INPUT ? handler.insert(slot, resource, amount, transaction) : 0;
		}
	}

	@Override
	public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
		if (side == null || side.equals(Direction.UP)) {
			return slot < SLOTS_INPUT ? handler.extract(slot, resource, amount, transaction) : 0;
		} else {
			return slot == SLOT_MEAL_OUTPUT ? handler.extract(slot, resource, amount, transaction) : 0;
		}
	}
}
