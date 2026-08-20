package com.mohistmc.mod.module.create.client.content.equipment.bell;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.content.equipment.bell.AbstractBellBlockEntity;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.api.visual.ShaderLightVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleDynamicVisual;
import com.mohistmc.mod.module.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getUpRotateAngle;

public class BellVisual<T extends AbstractBellBlockEntity> extends AbstractBlockEntityVisual<T> implements SimpleDynamicVisual, ShaderLightVisual {
    private final TransformedInstance bell;
    private final @Nullable Quaternionf rotate;

    public BellVisual(VisualizationContext ctx, T blockEntity, float partialTick, Model model) {
        super(ctx, blockEntity, partialTick);
        bell = instancerProvider().instancer(InstanceTypes.TRANSFORMED, model).createInstance();
        Direction facing = blockState.getValue(BellBlock.FACING);
        BellAttachType attachment = blockState.getValue(BellBlock.ATTACHMENT);
        if (attachment == BellAttachType.SINGLE_WALL || attachment == BellAttachType.DOUBLE_WALL) {
            rotate = getUpRotateAngle(AngleHelper.horizontalAngle(facing) + 90);
        } else {
            rotate = getUpRotateAngle(AngleHelper.horizontalAngle(facing));
        }
        transformModels(partialTick);
    }

    private void transformModels(float partialTick) {
        bell.setIdentityTransform().translate(getVisualPosition());
        if (blockEntity.isRinging) {
            bell.rotateCentered(
                BellRenderer.getSwingAngle(blockEntity.ringingTicks + partialTick),
                blockEntity.ringDirection.getCounterClockWise()
            );
        }
        if (rotate != null) {
            bell.rotateCentered(rotate);
        }
        bell.setChanged();
    }

    public static <T extends AbstractBellBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> new BellVisual<>(
            context,
            blockEntity,
            partialTick,
            Models.chunkPartial(partial)
        );
    }

    @Override
    public void beginFrame(Context ctx) {
        if (blockEntity.isRinging) {
            transformModels(ctx.partialTick());
        }
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(bell);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(bell);
    }

    @Override
    protected void _delete() {
        bell.delete();
    }
}
