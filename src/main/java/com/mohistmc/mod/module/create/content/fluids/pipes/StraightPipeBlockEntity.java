package com.mohistmc.mod.module.create.content.fluids.pipes;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.fluids.FluidPropagator;
import com.mohistmc.mod.module.create.content.fluids.FluidTransportBehaviour;
import com.mohistmc.mod.module.create.content.fluids.pipes.valve.FluidValveBlock;
import com.mohistmc.mod.module.create.foundation.advancement.CreateTrigger;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;

public class StraightPipeBlockEntity extends SmartBlockEntity {

    public StraightPipeBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.GLASS_FLUID_PIPE, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        behaviours.add(new StraightPipeFluidTransportBehaviour(this));
        behaviours.add(new BracketedBlockEntityBehaviour(this));
    }

    @Override
    public List<CreateTrigger> getAwardables() {
        return FluidPropagator.getSharedTriggers();
    }

    public static class StraightPipeFluidTransportBehaviour extends FluidTransportBehaviour {

        public StraightPipeFluidTransportBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return state.hasProperty(AxisPipeBlock.AXIS) && state.getValue(AxisPipeBlock.AXIS) == direction.getAxis();
        }

        @Override
        public AttachmentTypes getRenderedRimAttachment(
            BlockAndLightGetter world,
            BlockPos pos,
            BlockState state,
            Direction direction
        ) {
            AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);
            BlockState otherState = world.getBlockState(pos.relative(direction));

            Axis axis = IAxisPipe.getAxisOf(state);
            Axis otherAxis = IAxisPipe.getAxisOf(otherState);

            if (attachment == AttachmentTypes.RIM && state.getBlock() instanceof FluidValveBlock) {
                return AttachmentTypes.NONE;
            }
            if (attachment == AttachmentTypes.RIM && !(state.getBlock() instanceof GlassFluidPipeBlock) && otherState.getBlock() instanceof GlassFluidPipeBlock) {
                return AttachmentTypes.PARTIAL_RIM;
            }

            if (attachment == AttachmentTypes.RIM && FluidPipeBlock.isPipe(otherState)) {
                return AttachmentTypes.NONE;
            }
            if (axis == otherAxis && axis != null) {
                return AttachmentTypes.NONE;
            }

            if (otherState.getBlock() instanceof FluidValveBlock && FluidValveBlock.getPipeAxis(otherState) == direction.getAxis()) {
                return AttachmentTypes.NONE;
            }

            return attachment.withoutConnector();
        }

    }

}
