package com.mohistmc.mod.module.create.impl.unpacking;

import com.mohistmc.mod.module.create.api.packager.unpacking.UnpackingHandler;
import com.mohistmc.mod.module.create.foundation.item.ItemHelper;
import com.mohistmc.mod.module.create.infrastructure.component.PackageOrderWithCrafts;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class DefaultUnpackingHandler implements UnpackingHandler {
    @Override
    public boolean unpack(
        Level level,
        BlockPos pos,
        BlockState state,
        Direction side,
        List<ItemStack> items,
        @Nullable PackageOrderWithCrafts orderContext,
        boolean simulate
    ) {
        BlockEntity targetBE = level.getBlockEntity(pos);
        if (targetBE == null) {
            return false;
        }

        Container targetInv = ItemHelper.getInventory(level, pos, state, targetBE, side);
        if (targetInv == null) {
            return false;
        }

        if (!simulate) {
            targetInv.insert(items);
            return true;
        }
        return targetInv.countSpace(items);
    }
}