package com.mohistmc.mod.module.create.client.content.kinetics.drill;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.mohistmc.mod.module.create.content.kinetics.drill.DrillBlockEntity;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DrillVisual extends OrientedRotatingVisual<DrillBlockEntity> {
    public DrillVisual(VisualizationContext context, DrillBlockEntity blockEntity, float partialTick) {
        super(
            context,
            blockEntity,
            partialTick,
            Direction.SOUTH,
            blockEntity.getBlockState().getValue(BlockStateProperties.FACING),
            Models.chunkPartial(AllPartialModels.DRILL_HEAD)
        );
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(BlockStateProperties.FACING)) {
            case UP -> setSectionCollector(sectionCollector, 0, 0, 0, 0, 1, 0);
            case DOWN -> setSectionCollector(sectionCollector, 0, -1, 0, 0, 0, 0);
            case NORTH -> setSectionCollector(sectionCollector, 0, 0, -1, 0, 0, 0);
            case SOUTH -> setSectionCollector(sectionCollector, 0, 0, 0, 0, 0, 1);
            case WEST -> setSectionCollector(sectionCollector, -1, 0, 0, 0, 0, 0);
            case EAST -> setSectionCollector(sectionCollector, 0, 0, 0, 1, 0, 0);
        }
    }
}
