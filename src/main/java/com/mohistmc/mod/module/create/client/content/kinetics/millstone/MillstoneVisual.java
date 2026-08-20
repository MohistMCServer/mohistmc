package com.mohistmc.mod.module.create.client.content.kinetics.millstone;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.mohistmc.mod.module.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.Models;

public class MillstoneVisual extends SingleAxisRotatingVisual<MillstoneBlockEntity> {
    public MillstoneVisual(VisualizationContext context, MillstoneBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(AllPartialModels.MILLSTONE_COG));
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
    }
}
