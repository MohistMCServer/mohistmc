package com.mohistmc.mod.module.flywheel.impl.compat;

import com.mohistmc.mod.module.flywheel.api.visualization.BlockEntityVisualizer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

public final class SodiumCompat {
    public static final boolean ACTIVE = false;

    private SodiumCompat() {
    }

    @Nullable
    public static <T extends BlockEntity> Object onSetBlockEntityVisualizer(
        BlockEntityType<T> type,
        @Nullable BlockEntityVisualizer<? super T> oldVisualizer,
        @Nullable BlockEntityVisualizer<? super T> newVisualizer,
        @Nullable Object predicate
    ) {
        return null;
    }
}
