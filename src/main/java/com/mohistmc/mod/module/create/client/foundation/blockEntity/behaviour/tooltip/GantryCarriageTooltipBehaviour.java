package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.tooltip;

import com.mohistmc.mod.module.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.mohistmc.mod.module.create.content.contraptions.AssemblyException;
import com.mohistmc.mod.module.create.content.contraptions.gantry.GantryCarriageBlockEntity;

public class GantryCarriageTooltipBehaviour extends TooltipBehaviour<GantryCarriageBlockEntity> implements IDisplayAssemblyExceptions {
    public GantryCarriageTooltipBehaviour(GantryCarriageBlockEntity be) {
        super(be);
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return blockEntity.getLastAssemblyException();
    }
}
