package com.mohistmc.mod.module.create.content.logistics.crate;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.UnknownNullability;

public class CreativeCrateBlockEntity extends CrateBlockEntity implements Clearable {
    @UnknownNullability
    ServerFilteringBehaviour filtering;
    public BottomlessItemHandler inv;

    public CreativeCrateBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.CREATIVE_CRATE, pos, state);
        inv = new BottomlessItemHandler(filtering::getFilter);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        behaviours.add(filtering = new ServerFilteringBehaviour(this));
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        inv.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        inv.setChanged();
    }

    @Override
    public void clearContent() {
        filtering.setFilter(ItemStack.EMPTY);
    }
}