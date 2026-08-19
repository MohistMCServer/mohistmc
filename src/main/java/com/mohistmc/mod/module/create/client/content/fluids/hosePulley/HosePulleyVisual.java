package com.mohistmc.mod.module.create.client.content.fluids.hosePulley;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.AllSpriteShifts;
import com.mohistmc.mod.module.create.client.catnip.render.SpriteShiftEntry;
import com.mohistmc.mod.module.create.client.content.contraptions.pulley.AbstractPulleyVisual;
import com.mohistmc.mod.module.create.client.content.processing.burner.ScrollInstance;
import com.mohistmc.mod.module.flywheel.api.instance.Instancer;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.fluids.hosePulley.HosePulleyBlockEntity;

public class HosePulleyVisual extends AbstractPulleyVisual<HosePulleyBlockEntity> {
    public HosePulleyVisual(VisualizationContext dispatcher, HosePulleyBlockEntity blockEntity, float partialTick) {
        super(dispatcher, blockEntity, partialTick);
    }

    @Override
    protected Instancer<TransformedInstance> getRopeModel() {
        return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.chunkPartial(AllPartialModels.HOSE));
    }

    @Override
    protected Instancer<TransformedInstance> getMagnetModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.HOSE_MAGNET)
        );
    }

    @Override
    protected Instancer<TransformedInstance> getHalfMagnetModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.HOSE_HALF_MAGNET)
        );
    }

    @Override
    protected Instancer<ScrollInstance> getCoilModel() {
        return instancerProvider().instancer(
            AllInstanceTypes.SCROLLING,
            Models.chunkPartial(AllPartialModels.HOSE_COIL)
        );
    }

    @Override
    protected Instancer<TransformedInstance> getHalfRopeModel() {
        return instancerProvider().instancer(
            InstanceTypes.TRANSFORMED,
            Models.chunkPartial(AllPartialModels.HOSE_HALF)
        );
    }

    @Override
    protected float getOffset(float pt) {
        return blockEntity.getInterpolatedOffset(pt);
    }

    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected SpriteShiftEntry getCoilAnimation() {
        return AllSpriteShifts.HOSE_PULLEY_COIL;
    }

}
