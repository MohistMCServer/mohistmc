package com.mohistmc.mod.module.create.client.content.kinetics.crusher;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CrushingWheelVisual extends SingleAxisRotatingVisual<CrushingWheelBlockEntity> {
    public CrushingWheelVisual(VisualizationContext context, CrushingWheelBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(AllPartialModels.CRUSHING_WHEEL));
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(BlockStateProperties.AXIS)) {
            case X -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
            case Y -> setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
            case Z -> setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
        }
    }
}
