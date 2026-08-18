package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.tooltip;

import com.mohistmc.mod.module.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.mohistmc.mod.module.create.content.contraptions.AssemblyException;
import com.mohistmc.mod.module.create.content.contraptions.piston.LinearActuatorBlockEntity;

public class LinearActuatorTooltipBehaviour extends KineticTooltipBehaviour<LinearActuatorBlockEntity> implements IDisplayAssemblyExceptions {
    public LinearActuatorTooltipBehaviour(LinearActuatorBlockEntity be) {
        super(be);
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return blockEntity.getLastAssemblyException();
    }
}
