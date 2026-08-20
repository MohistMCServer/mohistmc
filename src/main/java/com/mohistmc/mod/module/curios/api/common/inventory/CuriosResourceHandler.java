package com.mohistmc.mod.module.curios.api.common.inventory;

import com.mohistmc.mod.module.curios.api.type.inventory.IDynamicStackHandler;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CuriosResourceHandler extends ItemStacksResourceHandler {

    final IDynamicStackHandler curioStacks;

    public CuriosResourceHandler(IDynamicStackHandler curioStacks) {
        super(curioStacks.getSlots());
        this.curioStacks = curioStacks;

        for (int i = 0; i < this.curioStacks.getSlots(); i++) {
            this.stacks.set(i, this.curioStacks.getStackInSlot(i));
        }
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        Objects.checkIndex(index, this.size());
        return this.curioStacks.getSlotLimit(index);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        Objects.checkIndex(index, this.size());
        return this.curioStacks.isItemValid(index, resource.toStack());
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        Objects.checkIndex(index, this.size());
        this.curioStacks.setStackInSlot(index, this.stacks.get(index));
        this.curioStacks.setPreviousStackInSlot(index, previousContents.copy());
    }
}
