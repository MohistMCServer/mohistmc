package com.mohistmc.mod.module.create.content.fluids.tank.storage;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.AllMountedStorageTypes;
import com.mohistmc.mod.module.create.api.contraption.storage.SyncedMountedStorage;
import com.mohistmc.mod.module.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.mohistmc.mod.module.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat;
import com.mohistmc.mod.module.create.content.contraptions.Contraption;
import com.mohistmc.mod.module.create.content.fluids.tank.FluidTankBlockEntity;
import com.mohistmc.mod.module.create.foundation.fluid.FluidTank;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FluidTankMountedStorage extends WrapperMountedFluidStorage<FluidTankMountedStorage.Handler> implements SyncedMountedStorage {
    public static final MapCodec<FluidTankMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidTankMountedStorage::getCapacity),
        FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidTankMountedStorage::getFluid)
    ).apply(i, FluidTankMountedStorage::new));

    private boolean dirty;

    protected FluidTankMountedStorage(MountedFluidStorageType<?> type, int capacity, FluidStack stack) {
        super(type);
        wrapped = new Handler(capacity, stack);
    }

    protected FluidTankMountedStorage(int capacity, FluidStack stack) {
        this(AllMountedStorageTypes.FLUID_TANK, capacity, stack);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof FluidTankBlockEntity tank && tank.isController()) {
            FluidTank inventory = tank.getTankInventory();
            // capacity shouldn't change, leave it
            inventory.setFluid(wrapped.getFluid());
        }
    }

    public FluidStack getFluid() {
        return wrapped.getFluid();
    }

    public int getCapacity() {
        return wrapped.getMaxAmountPerStack();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void markClean() {
        dirty = false;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        BlockEntity be = AllClientHandle.INSTANCE.getBlockEntityClientSide(contraption, localPos);
        if (!(be instanceof FluidTankBlockEntity tank)) {
            return;
        }

        FluidTank inv = tank.getTankInventory();
        inv.setFluid(getFluid());
        float fillLevel = inv.getFluid().getAmount() / (float) inv.getMaxAmountPerStack();
        if (tank.getFluidLevel() == null) {
            tank.setFluidLevel(LerpedFloat.linear().startWithValue(fillLevel));
        }
        tank.getFluidLevel().chase(fillLevel, 0.5, LerpedFloat.Chaser.EXP);
    }

    public static FluidTankMountedStorage fromTank(FluidTankBlockEntity tank) {
        // tank has update callbacks, make an isolated copy
        FluidTank inventory = tank.getTankInventory();
        return new FluidTankMountedStorage(inventory.getMaxAmountPerStack(), inventory.getFluid().copy());
    }

    public final class Handler extends FluidTank {
        public Handler(int capacity, FluidStack stack) {
            super(capacity);
            setFluid(stack);
        }

        @Override
        public void markDirty() {
            dirty = true;
        }
    }
}