package com.mohistmc.mod.module.create.client.content.decoration.steamWhistle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mohistmc.mod.module.create.catnip.math.AngleHelper;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBufferRenderState;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.animation.AnimationBehaviour;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.animation.WhistleAnimationBehaviour;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.mohistmc.mod.module.create.content.decoration.steamWhistle.WhistleBlock;
import com.mohistmc.mod.module.create.content.decoration.steamWhistle.WhistleBlock.WhistleSize;
import com.mohistmc.mod.module.create.content.decoration.steamWhistle.WhistleBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import static com.mohistmc.mod.module.create.client.content.kinetics.base.KineticBlockEntityRenderer.getYRotateAngle;

public class WhistleRenderer implements BlockEntityRenderer<WhistleBlockEntity, WhistleRenderer.WhistleRenderState> {
    public WhistleRenderer(Context context) {
    }

    @Override
    public WhistleRenderState createRenderState() {
        return new WhistleRenderState();
    }

    @Override
    public void extractRenderState(
        WhistleBlockEntity be,
        WhistleRenderState state,
        float tickProgress,
        Vec3 cameraPos,
        @Nullable CrumblingOverlay crumblingOverlay
    ) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        WhistleSize size = state.blockState.getValue(WhistleBlock.SIZE);
        WhistleAnimationBehaviour behaviour = (WhistleAnimationBehaviour) be.getBehaviour(AnimationBehaviour.TYPE);
        if (behaviour != null) {
            float offset = behaviour.animation.getValue(tickProgress);
            if (behaviour.animation.getChaseTarget() > 0 && behaviour.animation.getValue() > 0.5f) {
                float wiggleProgress = (AnimationTickHolder.getTicks(level) + tickProgress) / 8.0f;
                offset = (float) (offset - Math.sin(wiggleProgress * (2 * Mth.PI) * (4 - size.ordinal())) / 16.0f);
            }
            state.offset = offset * 0.25f;
        }
        state.model = CachedBuffers.partial(getMouthModel(size), state.blockState).cardinalLighting(level)
            .light(state.lightCoords).extractRenderState();
        state.yRot = getYRotateAngle(AngleHelper.horizontalAngle(state.blockState.getValue(WhistleBlock.FACING)));
    }

    @Override
    public void submit(
        WhistleRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraState
    ) {
        if (state.yRot != null) {
            matrices.rotateAround(state.yRot, 0.5f, 0.5f, 0.5f);
        }
        matrices.translate(0, state.offset, 0);
        state.model.submit(matrices, queue);
    }

    public static PartialModel getMouthModel(WhistleSize size) {
        return switch (size) {
            case LARGE -> AllPartialModels.WHISTLE_MOUTH_LARGE;
            case MEDIUM -> AllPartialModels.WHISTLE_MOUTH_MEDIUM;
            default -> AllPartialModels.WHISTLE_MOUTH_SMALL;
        };
    }

    public static class WhistleRenderState extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState model;
        public @Nullable Quaternionf yRot;
        public float offset;
    }
}
