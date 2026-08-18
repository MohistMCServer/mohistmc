package com.mohistmc.mod.module.create.client.content.redstone.diodes;

import com.mohistmc.mod.module.create.catnip.theme.Color;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.create.client.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.create.client.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.create.client.flywheel.lib.visual.SimpleTickableVisual;
import com.mohistmc.mod.module.create.content.redstone.diodes.BrassDiodeBlockEntity;
import java.util.function.Consumer;

public class BrassDiodeVisual extends AbstractBlockEntityVisual<BrassDiodeBlockEntity> implements SimpleTickableVisual {

    protected final TransformedInstance indicator;

    protected int previousState;

    public BrassDiodeVisual(VisualizationContext context, BrassDiodeBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        indicator = instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.partial(AllPartialModels.FLEXPEATER_INDICATOR)
        ).createInstance();

        indicator.setIdentityTransform().translate(getVisualPosition()).colorRgb(getColor()).setChanged();

        previousState = blockEntity.state;
    }

    @Override
    public void tick(Context context) {
        if (previousState == blockEntity.state) {
            return;
        }

        indicator.colorRgb(getColor());
        indicator.setChanged();

        previousState = blockEntity.state;
    }

    @Override
    public void updateLight(float partialTick) {
        relight(indicator);
    }

    @Override
    protected void _delete() {
        indicator.delete();
    }

    protected int getColor() {
        return Color.mixColors(0x2c0300, 0xcd0000, blockEntity.getProgress());
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(indicator);
    }
}
