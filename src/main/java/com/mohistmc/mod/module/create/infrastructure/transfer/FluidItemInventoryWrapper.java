package com.mohistmc.mod.module.create.infrastructure.transfer;

import com.mohistmc.mod.module.create.infrastructure.fluids.FluidItemInventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Item-backed fluid capability view. The ItemAccess is retained so capability
 * implementations can transactionally replace the held container.
 */
public final class FluidItemInventoryWrapper extends FluidInventoryWrapper implements FluidItemInventory {
    @SuppressWarnings("unused")
    private final ItemAccess access;
    private final ItemStack stack;

    private FluidItemInventoryWrapper(
        ResourceHandler<FluidResource> handler,
        ItemAccess access,
        ItemStack stack
    ) {
        super(handler);
        this.access = access;
        this.stack = stack;
    }

    public static FluidItemInventoryWrapper of(
        ResourceHandler<FluidResource> handler,
        ItemAccess access,
        ItemStack stack
    ) {
        return new FluidItemInventoryWrapper(handler, access, stack);
    }

    @Override
    public ItemStack getContainer() {
        return stack;
    }

    @Override
    public void close() {
    }
}
