package com.mohistmc.mod.module.create.foundation.fluid;

import com.mohistmc.mod.module.create.AllTransfer;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventory;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventoryProvider;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FluidInventoryCache implements Supplier<@Nullable FluidInventory> {
    public final ServerLevel world;
    public final Direction direction;
    public final BlockPos pos;
    public boolean cached;
    public @Nullable FluidInventory inventory;
    public @Nullable Supplier<@Nullable FluidInventory> getter = this::refresh;

    public FluidInventoryCache(ServerLevel world, BlockPos pos, Direction direction) {
        this.world = world;
        this.direction = direction;
        this.pos = pos;
    }

    @Override
    @Nullable
    public FluidInventory get() {
        if (cached) {
            return inventory;
        }
        return inventory = getter.get();
    }

    public void invalidate() {
        cached = false;
        getter = this::refresh;
    }

    @Nullable
    private FluidInventory refresh() {
        cached = true;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof FluidInventoryProvider<?> provider) {
            return provider.getFluidInventory(state, world, pos, null, direction);
        }
        getter = AllTransfer.getCacheFluidInventory(world, pos, direction);
        if (getter == null) {
            return null;
        }
        cached = false;
        return getter.get();
    }
}

