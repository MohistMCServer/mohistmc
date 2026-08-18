package com.mohistmc.mod.module.create.client.content.contraptions.actors.roller;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.InstancerProvider;
import com.mohistmc.mod.module.create.client.flywheel.api.visual.ShaderLightVisual;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.OrientedInstance;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.create.content.contraptions.actors.roller.RollerBlock;
import com.mohistmc.mod.module.create.content.contraptions.actors.roller.RollerBlockEntity;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.Nullable;

// Ponder does not support Visual, so it does not require animation.
public class RollerVisual extends AbstractBlockEntityVisual<RollerBlockEntity> implements ShaderLightVisual {
    private final OrientedInstance frame;
    private final OrientedInstance wheel;

    public RollerVisual(VisualizationContext ctx, RollerBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
        InstancerProvider instancerProvider = instancerProvider();
        Direction facing = blockState.getValue(RollerBlock.FACING);
        float angle = AngleHelper.horizontalAngle(facing);
        float x = visualPos.getX();
        float y = visualPos.getY();
        float z = visualPos.getZ();
        frame = instancerProvider.instancer(InstanceTypes.ORIENTED, Models.chunkPartial(AllPartialModels.ROLLER_FRAME))
            .createInstance().position(x, y, z).rotateDegrees(angle + 180, Direction.UP);
        frame.setChanged();
        Vec3i vec = facing.getUnitVec3i();
        x = x + vec.getX() * 0.5625f;
        y = y - 0.75f;
        z = z + vec.getZ() * 0.5625f;
        wheel = instancerProvider.instancer(InstanceTypes.ORIENTED, Models.chunkPartial(AllPartialModels.ROLLER_WHEEL))
            .createInstance().position(x, y, z).rotateDegrees(angle, Direction.UP).rotateYDegrees(90);
        wheel.setChanged();
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        switch (blockState.getValue(RollerBlock.FACING)) {
            case NORTH -> setSectionCollector(sectionCollector, 0, -1, -1, 0, 0, 0);
            case SOUTH -> setSectionCollector(sectionCollector, 0, -1, 0, 0, 0, 1);
            case WEST -> setSectionCollector(sectionCollector, -1, -1, 0, 0, 0, 0);
            case EAST -> setSectionCollector(sectionCollector, 0, -1, 0, 1, 0, 0);
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(frame);
        consumer.accept(wheel);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(frame, wheel);
    }

    @Override
    protected void _delete() {
        frame.delete();
        wheel.delete();
    }
}
