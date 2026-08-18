package com.mohistmc.mod.module.create.content.trains.bogey;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.AllBogeyStyles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class StandardBogeyBlockEntity extends AbstractBogeyBlockEntity {

    public StandardBogeyBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.BOGEY, pos, state);
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return AllBogeyStyles.STANDARD;
    }
}
