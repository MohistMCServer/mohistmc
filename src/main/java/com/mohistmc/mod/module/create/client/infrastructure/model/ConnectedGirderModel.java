package com.mohistmc.mod.module.create.client.infrastructure.model;

import com.mohistmc.mod.module.create.catnip.data.Iterate;
import com.mohistmc.mod.module.create.client.AllCTBehaviours;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.content.decoration.girder.GirderBlock;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ConnectedGirderModel extends CTModel {
    public ConnectedGirderModel(BlockState state, UnbakedRoot unbaked) {
        super(state, unbaked, AllCTBehaviours.METAL_GIRDER);
    }

    @Override
    public void addPartsWithInfo(
        BlockAndTintGetter world,
        BlockPos pos,
        BlockState state,
        RandomSource random,
        List<BlockStateModelPart> parts
    ) {
        super.addPartsWithInfo(world, pos, state, random, parts);
        for (Direction direction : Iterate.horizontalDirections) {
            if (GirderBlock.isConnected(world, pos, state, direction)) {
                AllPartialModels.METAL_GIRDER_BRACKETS.get(direction).get().collectParts(random, parts);
            }
        }
    }
}
