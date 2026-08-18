package com.mohistmc.mod.module.create.content.logistics.itemHatch;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ItemHatchBlockEntity extends SmartBlockEntity implements Clearable {

    public ServerFilteringBehaviour filtering;

    public ItemHatchBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.ITEM_HATCH, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        behaviours.add(filtering = new ServerFilteringBehaviour(this));
    }

    @Override
    public void clearContent() {
        filtering.setFilter(ItemStack.EMPTY);
    }
}
