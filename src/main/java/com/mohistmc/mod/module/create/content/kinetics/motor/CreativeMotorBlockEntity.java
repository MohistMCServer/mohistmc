package com.mohistmc.mod.module.create.content.kinetics.motor;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.AllBlocks;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeMotorBlockEntity extends GeneratingKineticBlockEntity {

    public static final int DEFAULT_SPEED = 16;
    public static final int MAX_SPEED = 256;

    public ServerScrollValueBehaviour generatedSpeed;

    public CreativeMotorBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.MOTOR, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        int max = MAX_SPEED;
        generatedSpeed = new ServerKineticScrollValueBehaviour(this);
        generatedSpeed.between(-max, max);
        generatedSpeed.setValue(DEFAULT_SPEED);
        generatedSpeed.withCallback(i -> updateGeneratedRotation());
        behaviours.add(generatedSpeed);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed()) {
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        if (!getBlockState().is(AllBlocks.CREATIVE_MOTOR)) {
            return 0;
        }
        return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(CreativeMotorBlock.FACING));
    }

}
