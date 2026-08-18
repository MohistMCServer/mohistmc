package com.mohistmc.mod.module.create.api.contraption.storage.fluid;

import com.mohistmc.mod.module.create.api.registry.CreateRegistries;
import com.mohistmc.mod.module.create.api.registry.SimpleRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class MountedFluidStorageType<T extends MountedFluidStorage> {
    public static final Codec<MountedFluidStorageType<?>> CODEC = CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE.byNameCodec();
    public static final SimpleRegistry<Block, MountedFluidStorageType<?>> REGISTRY = SimpleRegistry.create();

    public final MapCodec<? extends T> codec;

    protected MountedFluidStorageType(MapCodec<? extends T> codec) {
        this.codec = codec;
    }

    @Nullable
    public abstract T mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be);
}
