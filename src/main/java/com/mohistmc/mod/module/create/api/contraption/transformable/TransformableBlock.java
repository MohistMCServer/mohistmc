package com.mohistmc.mod.module.create.api.contraption.transformable;

import com.mohistmc.mod.module.create.content.contraptions.StructureTransform;
import net.minecraft.world.level.block.state.BlockState;

public interface TransformableBlock {
    BlockState transform(BlockState state, StructureTransform transform);
}
