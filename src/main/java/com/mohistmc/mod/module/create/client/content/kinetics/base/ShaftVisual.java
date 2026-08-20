package com.mohistmc.mod.module.create.client.content.kinetics.base;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.Models;

public class ShaftVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {
    public ShaftVisual(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(AllPartialModels.SHAFT));
    }
}
