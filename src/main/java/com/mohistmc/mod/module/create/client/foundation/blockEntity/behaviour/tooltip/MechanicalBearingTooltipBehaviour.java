package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.tooltip;

import com.mohistmc.mod.module.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.mohistmc.mod.module.create.client.foundation.item.TooltipHelper;
import com.mohistmc.mod.module.create.content.contraptions.AssemblyException;
import com.mohistmc.mod.module.create.content.contraptions.bearing.BearingBlock;
import com.mohistmc.mod.module.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalBearingTooltipBehaviour extends GeneratingKineticTooltipBehaviour<MechanicalBearingBlockEntity> implements IDisplayAssemblyExceptions {
    public MechanicalBearingTooltipBehaviour(MechanicalBearingBlockEntity be) {
        super(be);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        if (isPlayerSneaking) {
            return false;
        }
        if (!blockEntity.isWindmill() && blockEntity.getSpeed() == 0) {
            return false;
        }
        if (blockEntity.isRunning()) {
            return false;
        }
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof BearingBlock)) {
            return false;
        }

        BlockState attachedState = blockEntity.getLevel()
            .getBlockState(blockEntity.getBlockPos().relative(state.getValue(BearingBlock.FACING)));
        if (attachedState.canBeReplaced()) {
            return false;
        }
        TooltipHelper.addHint(tooltip, "hint.empty_bearing");
        return true;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return blockEntity.getLastAssemblyException();
    }
}
