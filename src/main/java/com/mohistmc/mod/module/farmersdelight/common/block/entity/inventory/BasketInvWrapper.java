package com.mohistmc.mod.module.farmersdelight.common.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.Basket;

public class BasketInvWrapper implements ResourceHandler<ItemResource>
{
	protected final IItemHandler itemHandler;
	protected final Basket basket;

	public BasketInvWrapper(Basket basket) {
		this.itemHandler = new InvWrapper(basket);
		this.basket = basket;
	}

	@Override
	public int size() {
		return itemHandler.getSlots();
	}

	@Override
	public ItemResource getResource(int slot) {
		return ItemResource.of(itemHandler.getStackInSlot(slot));
	}

	@Override
	public long getAmountAsLong(int slot) {
		return itemHandler.getStackInSlot(slot).getCount();
	}

	@Override
	public long getCapacityAsLong(int slot, ItemResource resource) {
		return itemHandler.getSlotLimit(slot);
	}

	@Override
	public boolean isValid(int slot, ItemResource resource) {
		return itemHandler.isItemValid(slot, resource.toStack(1));
	}

	@Override
	public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
		ItemStack stack = resource.toStack(amount);
		ItemStack remainder = itemHandler.insertItem(slot, stack, true);
		int inserted = amount - remainder.getCount();
		if (inserted > 0 && basket != null) {
			boolean wasEmpty = basket.isEmpty();
			itemHandler.insertItem(slot, stack, false);
			if (wasEmpty && !basket.isOnCustomCooldown()) {
				basket.setCooldown(8);
			}
		}
		return inserted;
	}

	@Override
	public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
		ItemStack extracted = itemHandler.extractItem(slot, amount, true);
		if (!extracted.isEmpty()) {
			itemHandler.extractItem(slot, amount, false);
		}
		return extracted.getCount();
	}
}
