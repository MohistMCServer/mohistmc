package com.mohistmc.mod.module.create.client.content.fluids.pipes.valve;

import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBufferRenderState;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.mohistmc.mod.module.create.content.fluids.pipes.valve.FluidValveBlock;
import com.mohistmc.mod.module.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.KINETIC_BLOCK;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.RAD_180;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.RAD_90;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotationAxisOf;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getYRotateAngle;
import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.shaft;

public class FluidValveRenderer implements BlockEntityRenderer<FluidValveBlockEntity, FluidValveRenderer.FluidValveRenderState> {
    public FluidValveRenderer(Context context) {
    }

    @Override
    public FluidValveRenderState createRenderState() {
        return new FluidValveRenderState();
    }

    @Override
    public void extractRenderState(
        FluidValveBlockEntity be,
        FluidValveRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        Axis shaftAxis = getRotationAxisOf(state.blockState);
        state.shaft = CachedBuffers.block(KINETIC_BLOCK, shaft(shaftAxis)).cardinalLighting(cardinalLighting)
            .light(state.lightCoords).color(getTintColor(be)).extractRenderState();
        state.angle = getRotateAngleWithoutBeOffset(shaftAxis, be, state, level);
        Direction facing = state.blockState.getValue(FluidValveBlock.FACING);
        state.yRot = getYRotateAngle(AngleHelper.horizontalAngle(facing));
        if (facing != Direction.UP) {
            state.xRot = new Quaternionf().rotateX(facing == Direction.DOWN ? RAD_180 : RAD_90);
        }
        Axis pipeAxis = FluidValveBlock.getPipeAxis(state.blockState);
        float pointerRotation = Mth.lerp(be.pointer.getValue(tickProgress), 0, -90);
        if (pipeAxis.isHorizontal() && shaftAxis == Axis.X || pipeAxis.isVertical()) {
            state.yRot2 = getYRotateAngle(90 + pointerRotation);
        } else {
            state.yRot2 = getYRotateAngle(pointerRotation);
        }
        state.pointer = CachedBuffers.partial(AllPartialModels.FLUID_VALVE_POINTER, state.blockState)
            .cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState();
    }

    @Override
    public void submit(
        FluidValveRenderState state,
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
        matrices.translate(0.5f, 0.5f, 0.5f);
        if (state.yRot != null) {
            matrices.mulPose(state.yRot);
        }
        if (state.xRot != null) {
            matrices.mulPose(state.xRot);
        }
        if (state.yRot2 != null) {
            matrices.mulPose(state.yRot2);
        }
        matrices.translate(-0.5f, -0.5f, -0.5f);
        state.pointer.submit(matrices, queue);
    }

    public static class FluidValveRenderState extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState shaft;
        public @Nullable Quaternionf angle;
        public @UnknownNullability SuperByteBufferRenderState pointer;
        public @Nullable Quaternionf yRot;
        public @Nullable Quaternionf xRot;
        public @Nullable Quaternionf yRot2;
    }
}
