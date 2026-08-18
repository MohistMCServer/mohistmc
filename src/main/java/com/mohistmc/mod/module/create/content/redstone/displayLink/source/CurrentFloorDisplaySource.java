package com.mohistmc.mod.module.create.content.redstone.displayLink.source;

import com.mohistmc.mod.module.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.mohistmc.mod.module.create.content.redstone.displayLink.DisplayLinkContext;
import com.mohistmc.mod.module.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CurrentFloorDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof ElevatorContactBlockEntity ecbe)) {
            return EMPTY_LINE;
        }
        return Component.literal(ecbe.lastReportedCurrentFloor);
    }

    @Override
    protected String getTranslationKey() {
        return "current_floor";
    }

    @Override
    public boolean allowsLabeling(DisplayLinkContext context) {
        return false;
    }

}
