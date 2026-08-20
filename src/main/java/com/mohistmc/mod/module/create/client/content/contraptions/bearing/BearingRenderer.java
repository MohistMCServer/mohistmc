package com.mohistmc.mod.module.create.client.content.contraptions.bearing;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBufferRenderState;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.mohistmc.mod.module.create.content.contraptions.bearing.IBearingBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getEastRotateAngle;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRadiansRotateAngle;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getUpRotateAngle;

public class BearingRenderer<T extends KineticBlockEntity & IBearingBlockEntity> implements BlockEntityRenderer<T, BearingRenderer.BearingRenderState> {
    public BearingRenderer(Context context) {
    }

    @Override
    public BearingRenderState createRenderState() {
        return new BearingRenderState();
    }

    @Override
    public void extractRenderState(
        T be,
        BearingRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        Axis axis = facing.getAxis();
        Direction direction = axis.getPositive();
        int color = getTintColor(be);
        Direction opposite = facing.getOpposite();
        state.angle = getRotateAngleWithoutBeOffset(axis, direction, be, state, level);
        state.shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, opposite)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        state.topAngle = getRadiansRotateAngle(
            (float) (be.getInterpolatedAngle(tickProgress - 1) / 180 * Math.PI),
            direction
        );
        state.eastAngle = getEastRotateAngle(-90 - AngleHelper.verticalAngle(facing));
        if (axis != Axis.Y) {
            state.upAngle = getUpRotateAngle(AngleHelper.horizontalAngle(opposite));
        }
        state.top = CachedBuffers.partial(
            be.isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP,
            state.blockState
        ).cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
    }

    @Override
    public void submit(
        BearingRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.angle != null) {
            matrices.pushPose();
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
            state.shaft.submit(matrices, queue);
            matrices.popPose();
        } else {
            state.shaft.submit(matrices, queue);
        }
        if (state.topAngle != null) {
            matrices.rotateAround(state.topAngle, 0.5f, 0.5f, 0.5f);
        }
        if (state.upAngle != null) {
            matrices.rotateAround(state.upAngle, 0.5f, 0.5f, 0.5f);
        }
        if (state.eastAngle != null) {
            matrices.rotateAround(state.eastAngle, 0.5f, 0.5f, 0.5f);
        }
        state.top.submit(matrices, queue);
    }

    public static class BearingRenderState extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState shaft;
        public @UnknownNullability SuperByteBufferRenderState top;
        public @Nullable Quaternionf angle;
        public @Nullable Quaternionf topAngle;
        public @Nullable Quaternionf upAngle;
        public @Nullable Quaternionf eastAngle;
    }
}
