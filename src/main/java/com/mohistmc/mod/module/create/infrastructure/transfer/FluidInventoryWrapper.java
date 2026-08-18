package com.mohistmc.mod.module.create.infrastructure.transfer;

import com.google.common.collect.MapMaker;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import java.util.Map;
import java.util.function.Predicate;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Adapts an external NeoForge fluid handler to Create's fluid inventory API.
 */
public class FluidInventoryWrapper implements FluidInventory {
    private static final Map<ResourceHandler<FluidResource>, FluidInventoryWrapper> CACHE =
        new MapMaker().weakKeys().weakValues().makeMap();

    protected final ResourceHandler<FluidResource> handler;

    protected FluidInventoryWrapper(ResourceHandler<FluidResource> handler) {
        this.handler = handler;
    }

    public static FluidInventoryWrapper of(ResourceHandler<FluidResource> handler) {
        return CACHE.computeIfAbsent(handler, FluidInventoryWrapper::new);
    }

    protected static FluidStack toCreateStack(FluidResource resource, int amount) {
        if (resource.isEmpty() || amount <= 0) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(resource.getFluid(), amount, resource.getComponentsPatch());
    }

    protected static FluidResource toResource(FluidStack stack) {
        return stack.isEmpty()
            ? FluidResource.EMPTY
            : FluidResource.of(stack.getFluid(), stack.getComponentChanges());
    }

    @Override
    public int size() {
        return handler.size();
    }

    @Override
    public FluidStack getStack(int slot) {
        return toCreateStack(handler.getResource(slot), handler.getAmountAsInt(slot));
    }

    @Override
    public void setStack(int slot, FluidStack stack) {
        if (slot < 0 || slot >= handler.size()) {
            return;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            FluidResource current = handler.getResource(slot);
            int amount = handler.getAmountAsInt(slot);
            if (!current.isEmpty() && amount > 0) {
                handler.extract(slot, current, amount, transaction);
            }
            if (!stack.isEmpty()
                && handler.insert(slot, toResource(stack), stack.getAmount(), transaction) != stack.getAmount()) {
                return;
            }
            transaction.commit();
        }
    }

    @Override
    public boolean isValid(int slot, FluidStack stack) {
        return !stack.isEmpty() && handler.isValid(slot, toResource(stack));
    }

    @Override
    public int getMaxAmount(FluidStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        FluidResource resource = toResource(stack);
        int maximum = 0;
        for (int i = 0; i < handler.size(); i++) {
            if (handler.isValid(i, resource)) {
                maximum = Math.max(maximum, handler.getCapacityAsInt(i, resource));
            }
        }
        return Math.min(stack.getMaxAmount(), maximum);
    }

    @Override
    public int count(FluidStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        FluidResource resource = toResource(stack);
        int amount = 0;
        for (int i = 0; i < handler.size() && amount < maxAmount; i++) {
            if (resource.equals(handler.getResource(i))) {
                amount += handler.getAmountAsInt(i);
            }
        }
        return Math.min(amount, maxAmount);
    }

    @Override
    public FluidStack count(Predicate<FluidStack> predicate, int maxAmount) {
        if (maxAmount <= 0) {
            return FluidStack.EMPTY;
        }
        for (int i = 0; i < handler.size(); i++) {
            FluidStack stack = getStack(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack.directCopy(Math.min(stack.getAmount(), maxAmount));
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int countSpace(FluidStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            return handler.insert(toResource(stack), maxAmount, transaction);
        }
    }

    @Override
    public int insert(FluidStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = handler.insert(toResource(stack), maxAmount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    @Override
    public int extract(FluidStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = handler.extract(toResource(stack), maxAmount, transaction);
            transaction.commit();
            return extracted;
        }
    }

    @Override
    public FluidStack extract(Predicate<FluidStack> predicate, int maxAmount) {
        FluidStack found = count(predicate, maxAmount);
        if (found.isEmpty()) {
            return FluidStack.EMPTY;
        }
        int extracted = extract(found, found.getAmount());
        return extracted == 0 ? FluidStack.EMPTY : found.directCopy(extracted);
    }

    @Override
    public FluidStack extractAny(int maxAmount) {
        return extract(stack -> true, maxAmount);
    }

    @Override
    public boolean preciseInsert(FluidStack stack, int maxAmount) {
        if (stack.isEmpty() || maxAmount <= 0) {
            return true;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (handler.insert(toResource(stack), maxAmount, transaction) != maxAmount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    @Override
    public boolean preciseExtract(FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (handler.extract(toResource(stack), stack.getAmount(), transaction) != stack.getAmount()) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    @Override
    public FluidStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= handler.size() || amount <= 0) {
            return FluidStack.EMPTY;
        }
        FluidResource resource = handler.getResource(slot);
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = handler.extract(slot, resource, amount, transaction);
            if (extracted == 0) {
                return FluidStack.EMPTY;
            }
            transaction.commit();
            return toCreateStack(resource, extracted);
        }
    }
}
