package com.mohistmc.mod.module.create.client.content.contraptions.bearing;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderBehaviour;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderState;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBufferRenderState;
import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.AbstractContraptionEntity;
import com.mohistmc.mod.module.create.content.contraptions.ControlledContraptionEntity;
import com.mohistmc.mod.module.create.content.contraptions.OrientedContraptionEntity;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class StabilizedBearingMovementRenderBehaviour implements MovementRenderBehaviour {
    @Nullable
    @Override
    public ActorVisual createVisual(
        VisualizationContext visualizationContext,
        VirtualRenderWorld simulationWorld,
        MovementContext movementContext
    ) {
        return new StabilizedBearingVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @Nullable
    public MovementRenderState getRenderState(
        Vec3 camera,
        Font textRenderer,
        MovementContext context,
        VirtualRenderWorld renderWorld,
        PoseStack.Pose transform,
        Matrix4f worldMatrix4f
    ) {
        if (VisualizationManager.supportsVisualization(context.world)) {
            return null;
        }
        Direction facing = context.state.getValue(BlockStateProperties.FACING);
        // rotate to match blockstate
        Quaternionf orientation = BearingVisual.getBlockStateOrientation(facing);
        // rotate against parent
        float angle = getCounterRotationAngle(
            context,
            facing,
            AnimationTickHolder.getPartialTicks()
        ) * facing.getAxisDirection().getStep();
        Quaternionf rotation = Axis.of(facing.step()).rotationDegrees(angle).mul(orientation);
        BlockPos pos = context.localPos;
        int light = LightCoordsUtil.getLightCoords(renderWorld, pos);
        SuperByteBufferRenderState top = CachedBuffers.partial(AllPartialModels.BEARING_TOP, context.state)
            .transform(transform).translate(pos).rotateCentered(rotation).light(light)
            .useLevelLight(context.world, worldMatrix4f).extractRenderState();
        return top::submit;
    }

    static float getCounterRotationAngle(MovementContext context, Direction facing, float renderPartialTicks) {
        if (!context.contraption.canBeStabilized(facing, context.localPos)) {
            return 0;
        }

        float offset = 0;
        Direction.Axis axis = facing.getAxis();
        AbstractContraptionEntity entity = context.contraption.entity;

        if (entity instanceof ControlledContraptionEntity controlledCE) {
            if (context.contraption.canBeStabilized(facing, context.localPos)) {
                offset = -controlledCE.getAngle(renderPartialTicks);
            }

        } else if (entity instanceof OrientedContraptionEntity orientedCE) {
            if (axis.isVertical()) {
                offset = -orientedCE.getViewYRot(renderPartialTicks);
            } else {
                if (orientedCE.isInitialOrientationPresent() && orientedCE.getInitialOrientation().getAxis() == axis) {
                    offset = -orientedCE.getViewXRot(renderPartialTicks);
                }
            }
        }
        return offset;
    }
}
