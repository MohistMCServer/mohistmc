package com.mohistmc.mod.module.create.client.content.contraptions.actors.contraptionControls;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.InstancerProvider;
import com.mohistmc.mod.module.create.client.flywheel.api.visual.ShaderLightVisual;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.OrientedInstance;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.create.client.flywheel.lib.visual.SimpleDynamicVisual;
import com.mohistmc.mod.module.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.mohistmc.mod.module.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ContraptionControlsVisual extends AbstractBlockEntityVisual<ContraptionControlsBlockEntity> implements SimpleDynamicVisual, ShaderLightVisual {
    private final OrientedInstance button;
    private final OrientedInstance indicator;
    private final Vec3 buttonMovementAxis;
    private boolean needUpdateIndicator;

    public ContraptionControlsVisual(
        VisualizationContext ctx,
        ContraptionControlsBlockEntity blockEntity,
        float partialTick
    ) {
        super(ctx, blockEntity, partialTick);
        InstancerProvider instancerProvider = instancerProvider();
        Direction facing = blockState.getValue(ContraptionControlsBlock.FACING).getOpposite();
        float horizontalAngle = AngleHelper.horizontalAngle(facing);
        button = instancerProvider.instancer(
            InstanceTypes.ORIENTED,
            Models.chunkPartial(AllPartialModels.CONTRAPTION_CONTROLS_BUTTON)
        ).createInstance().rotateYDegrees(horizontalAngle).rotateXDegrees(AngleHelper.verticalAngle(facing));
        buttonMovementAxis = VecHelper.rotate(new Vec3(0, 1, -0.325), horizontalAngle, Direction.Axis.Y);
        transformButton(partialTick);
        int i = (int) blockEntity.indicator.getValue(partialTick) / 45 % 8 + 8;
        indicator = instancerProvider.instancer(
            InstanceTypes.ORIENTED,
            Models.partial(AllPartialModels.CONTRAPTION_CONTROLS_INDICATOR.get(i % 8))
        ).createInstance();
        indicator.position(getVisualPosition()).rotation(button.rotation).setChanged();
        needUpdateIndicator = !blockEntity.indicator.settled();
    }

    private void transformButton(float partialTick) {
        BlockPos pos = getVisualPosition();
        float scale = -blockEntity.button.getValue(partialTick) / 24.0f;
        button.position(
            (float) (pos.getX() + buttonMovementAxis.x * scale),
            (float) (pos.getY() + buttonMovementAxis.y * scale),
            (float) (pos.getZ() + buttonMovementAxis.z * scale)
        ).setChanged();
    }

    private void updateIndicator(float partialTick) {
        int i = (int) blockEntity.indicator.getValue(partialTick) / 45 % 8 + 8;
        instancerProvider().instancer(
            InstanceTypes.ORIENTED,
            Models.partial(AllPartialModels.CONTRAPTION_CONTROLS_INDICATOR.get(i % 8))
        ).stealInstance(indicator);
    }

    @Override
    public void beginFrame(Context ctx) {
        if (!blockEntity.button.settled()) {
            transformButton(ctx.partialTick());
        }
        if (!blockEntity.indicator.settled()) {
            updateIndicator(ctx.partialTick());
            needUpdateIndicator = true;
        } else if (needUpdateIndicator) {
            updateIndicator(ctx.partialTick());
            needUpdateIndicator = false;
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(button);
        consumer.accept(indicator);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(button, indicator);
    }

    @Override
    protected void _delete() {
        button.delete();
        indicator.delete();
    }
}
