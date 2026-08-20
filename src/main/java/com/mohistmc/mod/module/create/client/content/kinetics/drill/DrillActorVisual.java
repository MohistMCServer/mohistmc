package com.mohistmc.mod.module.create.client.content.kinetics.drill;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import com.mohistmc.mod.module.create.content.kinetics.drill.DrillBlock;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class DrillActorVisual extends ActorVisual {
    TransformedInstance drillHead;
    private final Direction facing;

    private double rotation;
    private double previousRotation;

    public DrillActorVisual(
        VisualizationContext visualizationContext,
        VirtualRenderWorld contraption,
        MovementContext context
    ) {
        super(visualizationContext, contraption, context);

        BlockState state = context.state;

        facing = state.getValue(DrillBlock.FACING);

        drillHead = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.DRILL_HEAD))
            .createInstance();
    }

    @Override
    public void tick() {
        previousRotation = rotation;

        if (context.disabled || VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite())) {
            return;
        }

        float deg = context.getAnimationSpeed();

        rotation += deg / 20;

        rotation %= 360;
    }

    @Override
    public void beginFrame() {
        drillHead.setIdentityTransform().translate(context.localPos).center().rotateToFace(facing.getOpposite())
            .rotateZDegrees((float) getRotation()).uncenter().setChanged();
    }

    protected double getRotation() {
        return AngleHelper.angleLerp(AnimationTickHolder.getPartialTicks(), previousRotation, rotation);
    }

    @Override
    protected void _delete() {
        drillHead.delete();
    }
}
