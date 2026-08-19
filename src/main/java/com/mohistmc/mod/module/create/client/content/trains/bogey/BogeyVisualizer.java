package com.mohistmc.mod.module.create.client.content.trains.bogey;

import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;

@FunctionalInterface
public interface BogeyVisualizer {
    BogeyVisual createVisual(VisualizationContext ctx, float partialTick, boolean inContraption);
}
