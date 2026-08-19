package com.mohistmc.mod.module.flywheel.impl.visualization.storage;

import com.mohistmc.mod.module.flywheel.api.visual.Effect;
import com.mohistmc.mod.module.flywheel.api.visual.EffectVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;

public class EffectStorage extends Storage<Effect> {
    @Override
    protected EffectVisual<?> createRaw(VisualizationContext visualizationContext, Effect obj, float partialTick) {
        return obj.visualize(visualizationContext, partialTick);
    }

    @Override
    public boolean willAccept(Effect obj) {
        return true;
    }
}
