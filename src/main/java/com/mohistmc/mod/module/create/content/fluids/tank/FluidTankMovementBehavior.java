package com.mohistmc.mod.module.create.content.fluids.tank;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.api.behaviour.movement.MovementBehaviour;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.world.level.block.entity.BlockEntity;

// The fluid level needs to be ticked to animate smoothly
public class FluidTankMovementBehavior extends MovementBehaviour {
    @Override
    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide()) {
            BlockEntity be = AllClientHandle.INSTANCE.getBlockEntityClientSide(context.contraption, context.localPos);
            if (be instanceof FluidTankBlockEntity tank) {
                tank.getFluidLevel().tickChaser();
            }
        }
    }
}