package com.mohistmc.mod.module.create.client.content.kinetics.mixer;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.content.kinetics.base.RotatingInstance;
import com.mohistmc.mod.module.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.visual.DynamicVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.OrientedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleDynamicVisual;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.animation.AnimationBehaviour;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.animation.MechanicalMixerAnimationBehaviour;
import com.mohistmc.mod.module.create.client.foundation.render.AllInstanceTypes;
import com.mohistmc.mod.module.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import java.util.function.Consumer;
import net.minecraft.core.Direction.Axis;

public class MixerVisual extends SingleAxisRotatingVisual<MechanicalMixerBlockEntity> implements SimpleDynamicVisual {

    private final RotatingInstance mixerHead;
    private final OrientedInstance mixerPole;

    public MixerVisual(VisualizationContext context, MechanicalMixerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.chunkPartial(AllPartialModels.SHAFTLESS_COGWHEEL));

        mixerHead = instancerProvider().instancer(
            AllInstanceTypes.ROTATING,
            Models.chunkPartial(AllPartialModels.MECHANICAL_MIXER_HEAD)
        ).createInstance();

        mixerHead.setRotationAxis(Axis.Y);

        mixerPole = instancerProvider().instancer(
            InstanceTypes.ORIENTED,
            Models.chunkPartial(AllPartialModels.MECHANICAL_MIXER_POLE)
        ).createInstance();

        animate(partialTick);
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        setSectionCollector(sectionCollector, 0, -2, 0, 0, 1, 0);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        animate(ctx.partialTick());
    }

    private void animate(float pt) {
        float renderedHeadOffset = MechanicalMixerRenderer.getRenderedHeadOffset(blockEntity, pt);

        transformPole(renderedHeadOffset);
        transformHead(renderedHeadOffset, pt);
    }

    private void transformHead(float renderedHeadOffset, float pt) {
        MechanicalMixerAnimationBehaviour behaviour = (MechanicalMixerAnimationBehaviour) blockEntity.getBehaviour(
            AnimationBehaviour.TYPE);
        float speed = blockEntity.getSpeed();
        mixerHead.setPosition(getVisualPosition()).nudge(0, renderedHeadOffset, 0)
            .setRotationalSpeed(speed * RotatingInstance.SPEED_MULTIPLIER)
            .setRotationOffset(behaviour.getOffset(speed, pt)).setChanged();
    }

    private void transformPole(float renderedHeadOffset) {
        mixerPole.position(getVisualPosition()).translatePosition(0, renderedHeadOffset, 0).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);

        relight(pos.below(), mixerHead);
        relight(mixerPole);
    }

    @Override
    protected void _delete() {
        super._delete();
        mixerHead.delete();
        mixerPole.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(mixerHead);
        consumer.accept(mixerPole);
    }
}
