package com.mohistmc.mod.module.create.client.mixin;

import com.zurrtum.create.client.foundation.block.render.BlockDestructionProgressExtension;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockDestructionProgress.class)
public class BlockDestructionProgressMixin implements BlockDestructionProgressExtension {
    @Unique
    private Set<BlockPos> create$extraPositions;

    @Override
    public Set<BlockPos> create$getExtraPositions() {
        return create$extraPositions;
    }

    @Override
    public void create$setExtraPositions(Set<BlockPos> positions) {
        create$extraPositions = positions;
    }
}
