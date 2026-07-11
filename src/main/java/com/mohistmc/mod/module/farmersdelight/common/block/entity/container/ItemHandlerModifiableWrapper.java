package com.mohistmc.mod.module.farmersdelight.common.block.entity.container;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Wraps an ItemStacksResourceHandler as an IItemHandlerModifiable for use with SlotItemHandler.
 * NeoForge 26.2's IItemHandler.of() returns an adapter that only implements IItemHandler,
 * but SlotItemHandler.set() requires IItemHandlerModifiable.
 */
@ParametersAreNonnullByDefault
public class ItemHandlerModifiableWrapper implements IItemHandlerModifiable
{
    private final ItemStacksResourceHandler handler;

    public ItemHandlerModifiableWrapper(ItemStacksResourceHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        handler.set(slot, net.neoforged.neoforge.transfer.item.ItemResource.of(stack), stack.getCount());
    }

    @Override
    public int getSlots() {
        return handler.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getResource(slot).toStack(handler.getAmountAsInt(slot));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        var resource = net.neoforged.neoforge.transfer.item.ItemResource.of(stack);
        if (resource == null) return stack;
        try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
            int inserted = handler.insert(slot, resource, stack.getCount(), tx);
            if (!simulate) tx.commit();
            return stack.copyWithCount(stack.getCount() - inserted);
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        var resource = handler.getResource(slot);
        if (resource.isEmpty()) return ItemStack.EMPTY;
        try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
            int extracted = handler.extract(slot, resource, amount, tx);
            if (!simulate) tx.commit();
            return resource.toStack(extracted);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getCapacityAsInt(slot, handler.getResource(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        var resource = net.neoforged.neoforge.transfer.item.ItemResource.of(stack);
        return resource != null && handler.isValid(slot, resource);
    }
}
