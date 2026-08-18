package com.mohistmc.mod.module.create.client.foundation.block.render;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public interface BlockDestructionProgressExtension {
    @Nullable Set<BlockPos> create$getExtraPositions();

    void create$setExtraPositions(@Nullable Set<BlockPos> positions);
}
