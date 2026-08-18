package com.mohistmc.mod.module.create.impl.unpacking;

import com.mohistmc.mod.module.create.AllUnpackingHandlers;
import com.mohistmc.mod.module.create.api.packager.unpacking.UnpackingHandler;
import com.mohistmc.mod.module.create.content.processing.basin.BasinBlockEntity;
import com.mohistmc.mod.module.create.infrastructure.component.PackageOrderWithCrafts;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BasinUnpackingHandler implements UnpackingHandler {
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
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BasinBlockEntity basin)) {
            return false;
        }

        basin.itemCapability.disableCheck();

        try {
            return AllUnpackingHandlers.DEFAULT.unpack(level, pos, state, side, items, orderContext, simulate);
        } finally {
            basin.itemCapability.enableCheck();
        }
    }
}
