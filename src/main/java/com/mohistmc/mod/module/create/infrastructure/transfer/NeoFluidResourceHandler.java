package com.mohistmc.mod.module.create.infrastructure.transfer;

import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventory;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidStack;
import com.mohistmc.mod.module.create.infrastructure.fluids.SidedFluidInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

/**
 * Transaction-aware NeoForge capability backed by Create's internal fluid
 * inventory.
 */
public final class NeoFluidResourceHandler extends SnapshotJournal<List<FluidStack>>
    implements ResourceHandler<FluidResource> {
    private final FluidInventory inventory;
    private final @Nullable Direction side;

    public NeoFluidResourceHandler(FluidInventory inventory, @Nullable Direction side) {
        this.inventory = inventory;
        this.side = side;
    }

    private int actualIndex(int index) {
        if (inventory instanceof SidedFluidInventory sided && side != null) {
            return sided.getAvailableSlots(side)[index];
        }
        return index;
    }

    private static FluidStack toCreateStack(FluidResource resource, int amount) {
        return resource.isEmpty() || amount <= 0
            ? FluidStack.EMPTY
            : new FluidStack(resource.getFluid(), amount, resource.getComponentsPatch());
    }

    private static FluidResource toResource(FluidStack stack) {
        return stack.isEmpty()
            ? FluidResource.EMPTY
            : FluidResource.of(stack.getFluid(), stack.getComponentChanges());
    }

    @Override
    public int size() {
        if (inventory instanceof SidedFluidInventory sided && side != null) {
            return sided.getAvailableSlots(side).length;
        }
        return inventory.size();
    }

    @Override
    public FluidResource getResource(int index) {
        return toResource(inventory.getStack(actualIndex(index)));
    }

    @Override
    public long getAmountAsLong(int index) {
        return inventory.getStack(actualIndex(index)).getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        if (resource.isEmpty()) {
            FluidStack current = inventory.getStack(actualIndex(index));
            return current.isEmpty() ? Integer.MAX_VALUE : inventory.getMaxAmount(current);
        }
        FluidStack stack = toCreateStack(resource, 1);
        return isValid(index, resource) ? inventory.getMaxAmount(stack) : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        if (resource.isEmpty()) {
            return false;
        }
        int actual = actualIndex(index);
        FluidStack stack = toCreateStack(resource, 1);
        if (!inventory.isValid(actual, stack)) {
            return false;
        }
        return !(inventory instanceof SidedFluidInventory sided) || sided.canInsert(actual, stack, side);
    }

    @Override
    public int insert(
        int index,
        FluidResource resource,
        int amount,
        TransactionContext transaction
    ) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount == 0 || !isValid(index, resource)) {
            return 0;
        }
        int actual = actualIndex(index);
        FluidStack current = inventory.getStack(actual);
        if (!current.isEmpty() && !resource.equals(toResource(current))) {
            return 0;
        }
        FluidStack incoming = toCreateStack(resource, amount);
        int capacity = inventory.getMaxAmount(incoming);
        int inserted = Math.min(amount, Math.max(0, capacity - current.getAmount()));
        if (inserted == 0) {
            return 0;
        }
        updateSnapshots(transaction);
        if (current.isEmpty()) {
            inventory.setStack(actual, incoming.directCopy(inserted));
        } else {
            current.setAmount(current.getAmount() + inserted);
        }
        return inserted;
    }

    @Override
    public int extract(
        int index,
        FluidResource resource,
        int amount,
        TransactionContext transaction
    ) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount == 0) {
            return 0;
        }
        int actual = actualIndex(index);
        FluidStack current = inventory.getStack(actual);
        if (current.isEmpty() || !resource.equals(toResource(current))) {
            return 0;
        }
        if (inventory instanceof SidedFluidInventory sided && !sided.canExtract(actual, current, side)) {
            return 0;
        }
        int extracted = Math.min(amount, current.getAmount());
        if (extracted == 0) {
            return 0;
        }
        updateSnapshots(transaction);
        if (extracted == current.getAmount()) {
            inventory.setStack(actual, FluidStack.EMPTY);
        } else {
            current.setAmount(current.getAmount() - extracted);
        }
        return extracted;
    }

    @Override
    protected List<FluidStack> createSnapshot() {
        List<FluidStack> snapshot = new ArrayList<>(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            snapshot.add(inventory.getStack(i).copy());
        }
        return snapshot;
    }

    @Override
    protected void revertToSnapshot(List<FluidStack> snapshot) {
        int size = Math.min(snapshot.size(), inventory.size());
        for (int i = 0; i < size; i++) {
            inventory.setStack(i, snapshot.get(i));
        }
        for (int i = size; i < inventory.size(); i++) {
            inventory.setStack(i, FluidStack.EMPTY);
        }
    }

    @Override
    protected void onRootCommit(List<FluidStack> originalState) {
        inventory.markDirty();
    }
}
