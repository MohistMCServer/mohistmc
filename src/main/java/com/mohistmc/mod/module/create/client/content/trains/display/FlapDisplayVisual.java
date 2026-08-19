package com.mohistmc.mod.module.create.client.content.trains.display;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.content.trains.display.FlapDisplayBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlapDisplayVisual extends SingleAxisRotatingVisual<FlapDisplayBlockEntity> {
    public FlapDisplayVisual(VisualizationContext context, FlapDisplayBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(AllPartialModels.SHAFTLESS_COGWHEEL));
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis()) {
            case X -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
            case Z -> setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
        }
    }
}
