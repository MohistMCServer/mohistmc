package com.mohistmc.mod.module.create.client.infrastructure.model;

import com.mohistmc.mod.module.flywheel.lib.model.baked.VirtualBlockGetter;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class GantryShaftModel extends WrapperBlockStateModel {
    public GantryShaftModel(BlockState state, UnbakedRoot unbaked) {
        super(state, unbaked);
    }

    @Override
    public void addPartsWithInfo(
        BlockAndTintGetter world,
        BlockPos pos,
        BlockState state,
        RandomSource random,
        List<BlockStateModelPart> parts
    ) {
        if (world instanceof VirtualBlockGetter) {
            model.collectParts(random, parts);
        }
    }
}
