package com.mohistmc.mod.module.create.client.infrastructure.model;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.flywheel.lib.model.baked.VirtualBlockGetter;
import com.mohistmc.mod.module.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BracketedKineticBlockModel extends WrapperBlockStateModel {
    public BracketedKineticBlockModel(BlockState state, UnbakedRoot unbaked) {
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
        BracketedBlockEntityBehaviour attachmentBehaviour = BlockEntityBehaviour.get(
            world,
            pos,
            BracketedBlockEntityBehaviour.TYPE
        );
        if (attachmentBehaviour == null) {
            addVirtualParts(world, random, parts);
            return;
        }
        BlockState bracket = attachmentBehaviour.getBracket();
        if (bracket == null) {
            addVirtualParts(world, random, parts);
            return;
        }
        Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(bracket).collectParts(random, parts);
    }

    private void addVirtualParts(BlockAndTintGetter world, RandomSource random, List<BlockStateModelPart> parts) {
        if (world instanceof VirtualBlockGetter) {
            model.collectParts(random, parts);
        }
    }
}
