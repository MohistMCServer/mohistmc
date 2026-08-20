package com.mohistmc.mod.module.create.client.content.kinetics.drill;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderBehaviour;
import com.mohistmc.mod.module.create.client.api.behaviour.movement.MovementRenderState;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBufferRenderState;
import com.mohistmc.mod.module.create.client.content.contraptions.render.ActorVisual;
import com.mohistmc.mod.module.create.client.foundation.virtualWorld.VirtualRenderWorld;
import com.mohistmc.mod.module.create.content.contraptions.behaviour.MovementContext;
import com.mohistmc.mod.module.create.content.kinetics.drill.DrillBlock;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class DrillMovementRenderBehaviour implements MovementRenderBehaviour {
    @Nullable
    @Override
    public ActorVisual createVisual(
        VisualizationContext visualizationContext,
        VirtualRenderWorld simulationWorld,
        MovementContext movementContext
    ) {
        return new DrillActorVisual(visualizationContext, simulationWorld, movementContext);
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
        BlockPos pos = context.localPos;
        BlockState blockState = context.state;
        Direction facing = blockState.getValue(DrillBlock.FACING);
        float speed = context.contraption.stalled || !VecHelper.isVecPointingTowards(
            context.relativeMotion,
            facing.getOpposite()
        ) ? context.getAnimationSpeed() : 0;
        float time = AnimationTickHolder.getRenderTime() / 20;
        float yRot = Mth.DEG_TO_RAD * AngleHelper.horizontalAngle(facing);
        float xRot = Mth.DEG_TO_RAD * AngleHelper.verticalAngle(facing);
        float zRot = Mth.DEG_TO_RAD * (time * speed % 360);
        int light = LightCoordsUtil.getLightCoords(renderWorld, pos);
        SuperByteBufferRenderState head = CachedBuffers.partial(AllPartialModels.DRILL_HEAD, blockState)
            .transform(transform).translate(pos).center().rotateY(yRot).rotateX(xRot).rotateZ(zRot).uncenter()
            .light(light).useLevelLight(context.world, worldMatrix4f).extractRenderState();
        return head::submit;
    }
}
