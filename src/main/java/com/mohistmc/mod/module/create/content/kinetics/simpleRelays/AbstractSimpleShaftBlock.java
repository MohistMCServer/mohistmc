package com.mohistmc.mod.module.create.content.kinetics.simpleRelays;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.equipment.wrench.IWrenchableWithBracket;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSimpleShaftBlock extends AbstractShaftBlock implements IWrenchableWithBracket {

    public AbstractSimpleShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return IWrenchableWithBracket.super.onWrenched(state, context);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean isMoving) {
        if (!isMoving) {
            removeBracket(world, pos, true).ifPresent(stack -> popResource(world, pos, stack));
        }
    }

    @Override
    public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
        BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get(
            world,
            pos,
            BracketedBlockEntityBehaviour.TYPE
        );
        if (behaviour == null) {
            return Optional.empty();
        }
        BlockState bracket = behaviour.removeBracket(inOnReplacedContext);
        if (bracket == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack(bracket.getBlock()));
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.BRACKETED_KINETIC;
    }

}
