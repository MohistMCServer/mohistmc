package com.mohistmc.mod.module.create.content.equipment.bell;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PeculiarBellBlockEntity extends AbstractBellBlockEntity {
    public PeculiarBellBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.PECULIAR_BELL, pos, state);
    }
}
