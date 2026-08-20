package com.mohistmc.mod.module.create.client.content.contraptions.actors.harvester;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderBehaviour;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderState;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBuffer;
import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationManager;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class HarvesterMovementRenderBehaviour implements MovementRenderBehaviour {
    @Nullable
    @Override
    public ActorVisual createVisual(
        VisualizationContext visualizationContext,
        VirtualRenderWorld simulationWorld,
        MovementContext movementContext
    ) {
        return new HarvesterActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @Nullable
    public MovementRenderState getRenderState(
        Vec3 camera,
        Font textRenderer,
        MovementContext context,
        VirtualRenderWorld renderWorld,
        Pose transform,
        Matrix4f worldMatrix4f
    ) {
        if (VisualizationManager.supportsVisualization(context.world)) {
            return null;
        }
        BlockState blockState = context.state;
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        int light = LightCoordsUtil.getLightCoords(renderWorld, context.localPos);
        SuperByteBuffer model = CachedBuffers.partial(AllPartialModels.HARVESTER_BLADE, blockState).transform(transform)
            .translate(context.localPos)
            .rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing)), Direction.UP);
        float time = AnimationTickHolder.getRenderTime(context.world) / 20;
        float speed = !VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite()) ?
            context.getAnimationSpeed() : 0;
        if (context.contraption.stalled) {
            speed = 0;
        }
        float angle = time * speed % 360;
        if (angle != 0) {
            model.translate(0, 0.375f, 0.5625f).rotate(AngleHelper.rad(angle), Direction.WEST)
                .translate(0, -0.375f, -0.5625f);
        }
        model.light(light).useLevelLight(context.world, worldMatrix4f);
        return model.extractRenderState()::submit;
    }
}
