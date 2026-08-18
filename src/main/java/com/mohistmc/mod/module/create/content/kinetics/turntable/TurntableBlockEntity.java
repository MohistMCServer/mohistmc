package com.mohistmc.mod.module.create.content.kinetics.turntable;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TurntableBlockEntity extends KineticBlockEntity {
    public TurntableBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.TURNTABLE, pos, state);
    }
}
