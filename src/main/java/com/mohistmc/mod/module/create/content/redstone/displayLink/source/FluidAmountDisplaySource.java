package com.mohistmc.mod.module.create.content.redstone.displayLink.source;

import com.mohistmc.mod.module.create.content.redstone.displayLink.DisplayLinkContext;
import com.mohistmc.mod.module.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.mohistmc.mod.module.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import com.mohistmc.mod.module.create.foundation.utility.FluidFormatter;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventory;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidAmountDisplaySource extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof SmartObserverBlockEntity cobe)) {
            return EMPTY_LINE;
        }

        TankManipulationBehaviour tankManipulationBehaviour = cobe.getBehaviour(TankManipulationBehaviour.OBSERVE);
        ServerFilteringBehaviour filteringBehaviour = cobe.getBehaviour(ServerFilteringBehaviour.TYPE);
        FluidInventory handler = tankManipulationBehaviour.getInventory();

        if (handler == null) {
            return EMPTY_LINE;
        }

        long collected = 0;
        for (int i = 0, size = handler.size(); i < size; i++) {
            FluidStack stack = handler.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!filteringBehaviour.test(stack)) {
                continue;
            }
            collected += stack.getAmount();
        }

        return Component.literal(FluidFormatter.asString(collected, false));
    }

    @Override
    protected String getTranslationKey() {
        return "fluid_amount";
    }

    @Override
    public boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
