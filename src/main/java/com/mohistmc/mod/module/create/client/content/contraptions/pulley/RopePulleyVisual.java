package com.mohistmc.mod.module.create.client.content.contraptions.pulley;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.AllSpriteShifts;
import com.mohistmc.mod.module.create.client.catnip.render.SpriteShiftEntry;
import com.mohistmc.mod.module.create.client.content.processing.burner.ScrollInstance;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.contraptions.pulley.PulleyBlockEntity;
import com.mohistmc.mod.module.flywheel.api.instance.Instancer;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;

public class RopePulleyVisual extends AbstractPulleyVisual<PulleyBlockEntity> {
    public RopePulleyVisual(VisualizationContext context, PulleyBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    protected Instancer<TransformedInstance> getRopeModel() {
        return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.chunkPartial(AllPartialModels.ROPE));
    }

    @Override
    protected Instancer<TransformedInstance> getMagnetModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.PULLEY_MAGNET)
        );
    }

    @Override
    protected Instancer<TransformedInstance> getHalfMagnetModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.ROPE_HALF_MAGNET)
        );
    }

    @Override
    protected Instancer<ScrollInstance> getCoilModel() {
        return instancerProvider().instancer(
            AllInstanceTypes.SCROLLING,
            Models.chunkPartial(AllPartialModels.ROPE_COIL)
        );
    }

    @Override
    protected Instancer<TransformedInstance> getHalfRopeModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.ROPE_HALF)
        );
    }

    @Override
    protected float getOffset(float pt) {
        return PulleyRenderer.getBlockEntityOffset(pt, blockEntity);
    }

    @Override
    protected boolean isRunning() {
        return PulleyRenderer.isPulleyRunning(blockEntity);
    }

    @Override
    protected SpriteShiftEntry getCoilAnimation() {
        return AllSpriteShifts.ROPE_PULLEY_COIL;
    }

}