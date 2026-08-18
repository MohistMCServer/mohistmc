package com.mohistmc.mod.module.create.client.content.kinetics.speedController;


import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.mohistmc.mod.module.create.content.kinetics.speedController.SpeedControllerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ControllerValueBoxTransform extends ValueBoxTransform.Sided {

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace(8, 11.0f, 15.5f);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        if (direction.getAxis().isVertical()) {
            return false;
        }
        return state.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) != direction.getAxis();
    }

}