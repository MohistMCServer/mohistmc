package com.mohistmc.mod.module.create.client.content.kinetics.base;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleTickableVisual;
import com.mohistmc.mod.module.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class SingleAxisRotatingVisual<T extends KineticBlockEntity> extends KineticBlockEntityVisual<T> implements SimpleTickableVisual {
    protected final RotatingInstance rotatingModel;

    public SingleAxisRotatingVisual(VisualizationContext context, T blockEntity, float partialTick, Model model) {
        this(context, blockEntity, partialTick, Direction.UP, model);
    }

    /**
     * @param from  The source model orientation to rotate away from.
     * @param model The model to spin.
     */
    public SingleAxisRotatingVisual(
        VisualizationContext context,
        T blockEntity,
        float partialTick,
        Direction from,
        Model model
    ) {
        super(context, blockEntity, partialTick);
        rotatingModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, model).createInstance()
            .rotateToFace(from, rotationAxis()).setup(blockEntity).setPosition(getVisualPosition());

        rotatingModel.setChanged();
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> new SingleAxisRotatingVisual<>(
            context,
            blockEntity,
            partialTick,
            Models.chunkPartial(partial)
        );
    }

    /**
     * For partial models whose source model is aligned with the Z axis instead of Y
     */
    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> ofZ(PartialModel partial) {
        return (context, blockEntity, partialTick) -> new SingleAxisRotatingVisual<>(
            context,
            blockEntity,
            partialTick,
            Direction.SOUTH,
            Models.chunkPartial(partial)
        );
    }

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> shaft(
        VisualizationContext context,
        T blockEntity,
        float partialTick
    ) {
        return new SingleAxisRotatingVisual<>(
            context,
            blockEntity,
            partialTick,
            Models.chunkPartial(AllPartialModels.SHAFT)
        );
    }

    @Override
    public void update(float pt) {
        rotatingModel.setup(blockEntity).setChanged();
    }

    @Override
    public void tick(Context context) {
        applyOverstressEffect(blockEntity, rotatingModel);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(rotatingModel);
    }
}
