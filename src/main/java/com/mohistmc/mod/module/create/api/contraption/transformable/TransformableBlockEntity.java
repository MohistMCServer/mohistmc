package com.mohistmc.mod.module.create.api.contraption.transformable;

import com.mohistmc.mod.module.create.content.contraptions.StructureTransform;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TransformableBlockEntity {
    void transform(BlockEntity blockEntity, StructureTransform transform);
}
