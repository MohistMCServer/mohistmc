package com.mohistmc.mod.module.create.infrastructure.transfer;

import com.google.common.collect.MapMaker;
import com.mohistmc.mod.module.create.infrastructure.items.ItemInventory;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Presents a NeoForge item transfer handler as Create's vanilla-style
 * inventory abstraction.
 */
public final class InventoryWrapper implements ItemInventory {
    private static final Map<ResourceHandler<ItemResource>, InventoryWrapper> CACHE =
        new MapMaker().weakKeys().weakValues().makeMap();

    private final ResourceHandler<ItemResource> handler;

    private InventoryWrapper(ResourceHandler<ItemResource> handler) {
        this.handler = handler;
    }

    public static InventoryWrapper of(ResourceHandler<ItemResource> handler) {
        return CACHE.computeIfAbsent(handler, InventoryWrapper::new);
    }

    @Override
    public int getContainerSize() {
        return handler.size();
    }

    @Override
    public ItemStack getItem(int slot) {
        ItemResource resource = handler.getResource(slot);
        return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(handler.getAmountAsInt(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= handler.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemResource resource = handler.getResource(slot);
        if (resource.isEmpty()) {
            return ItemStack.EMPTY;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = handler.extract(slot, resource, amount, transaction);
            if (extracted == 0) {
                return ItemStack.EMPTY;
            }
            transaction.commit();
            return resource.toStack(extracted);
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return removeItem(slot, Integer.MAX_VALUE);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= handler.size()) {
            return;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            ItemResource current = handler.getResource(slot);
            int currentAmount = handler.getAmountAsInt(slot);
            if (!current.isEmpty() && currentAmount > 0) {
                handler.extract(slot, current, currentAmount, transaction);
            }
            if (!stack.isEmpty()) {
                int inserted = handler.insert(slot, ItemResource.of(stack), stack.getCount(), transaction);
                if (inserted != stack.getCount()) {
                    return;
                }
            }
            transaction.commit();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !stack.isEmpty() && handler.isValid(slot, ItemResource.of(stack));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if (stack.isEmpty()) {
            return 64;
        }
        ItemResource resource = ItemResource.of(stack);
        int capacity = 0;
        for (int i = 0; i < handler.size(); i++) {
            if (handler.isValid(i, resource)) {
                capacity = Math.max(capacity, handler.getCapacityAsInt(i, resource));
            }
        }
        return Math.min(stack.getMaxStackSize(), capacity);
    }

    public int count(ItemStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        ItemResource resource = ItemResource.of(stack);
        int amount = 0;
        for (int i = 0; i < handler.size() && amount < maxAmount; i++) {
            if (resource.equals(handler.getResource(i))) {
                amount += handler.getAmountAsInt(i);
            }
        }
        return Math.min(amount, maxAmount);
    }

    public ItemStack count(Predicate<ItemStack> predicate, int maxAmount) {
        if (maxAmount <= 0) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack.copyWithCount(Math.min(stack.getCount(), maxAmount));
            }
        }
        return ItemStack.EMPTY;
    }

    public int countSpace(ItemStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            return handler.insert(ItemResource.of(stack), maxAmount, transaction);
        }
    }

    public int insert(ItemStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = handler.insert(ItemResource.of(stack), maxAmount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    public int extract(ItemStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = handler.extract(ItemResource.of(stack), maxAmount, transaction);
            transaction.commit();
            return extracted;
        }
    }

    public boolean preciseInsert(ItemStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return true;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (handler.insert(ItemResource.of(stack), maxAmount, transaction) != maxAmount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    public boolean preciseExtract(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (handler.extract(ItemResource.of(stack), stack.getCount(), transaction) != stack.getCount()) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }
}
