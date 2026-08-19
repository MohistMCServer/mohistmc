package com.mohistmc.mod.module.create.client.content.trains.signal;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.AllTrackRenders;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackBlockRenderer;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.visual.ShaderLightVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleTickableVisual;
import com.mohistmc.mod.module.create.content.trains.signal.SignalBlockEntity;
import com.mohistmc.mod.module.create.content.trains.signal.SignalBlockEntity.OverlayState;
import com.mohistmc.mod.module.create.content.trains.signal.SignalBlockEntity.SignalState;
import com.mohistmc.mod.module.create.content.trains.signal.SignalBoundary;
import com.mohistmc.mod.module.create.content.trains.track.ITrackBlock;
import com.mohistmc.mod.module.create.content.trains.track.TrackTargetingBehaviour;
import com.mohistmc.mod.module.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class SignalVisual extends AbstractBlockEntityVisual<SignalBlockEntity> implements SimpleTickableVisual, ShaderLightVisual {
    private final TransformedInstance signalLight;
    private final TransformedInstance signalOverlay;

    private boolean previousIsRedLight;
    private @Nullable OverlayState previousOverlayState;

    public SignalVisual(VisualizationContext ctx, SignalBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        signalLight = ctx.instancerProvider()
            .instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SIGNAL_OFF)).createInstance();

        signalOverlay = ctx.instancerProvider()
            .instancer(InstanceTypes.TRANSFORMED, Models.chunkPartial(AllPartialModels.TRACK_SIGNAL_OVERLAY))
            .createInstance();

        setupVisual();
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        if (previousOverlayState != null) {
            lightSections = sectionCollector;
            LongSet longSet = new LongArraySet();
            longSet.add(SectionPos.asLong(pos));
            longSet.add(SectionPos.asLong(blockEntity.edgePoint.getGlobalPosition()));
            lightSections.sections(longSet);
        } else {
            super.setSectionCollector(sectionCollector);
        }
    }

    @Override
    public void tick(Context context) {
        setupVisual();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(signalLight, signalOverlay);
    }

    @Override
    protected void _delete() {
        signalLight.delete();
        signalOverlay.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(signalLight);
    }

    private void setupVisual() {
        SignalState signalState = blockEntity.getState();

        float renderTime = AnimationTickHolder.getRenderTime(blockEntity.getLevel());
        boolean isRedLight = signalState.isRedLight(renderTime);

        if (isRedLight != previousIsRedLight) {
            PartialModel partial = isRedLight ? AllPartialModels.SIGNAL_ON : AllPartialModels.SIGNAL_OFF;
            instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(partial))
                .stealInstance(signalLight);
        }

        signalLight.setIdentityTransform().translate(getVisualPosition());

        if (isRedLight) {
            signalLight.light(LightCoordsUtil.MAX_SMOOTH_LIGHT_LEVEL);
        }

        signalLight.setChanged();

        previousIsRedLight = isRedLight;

        OverlayState overlayState = blockEntity.getOverlay();

        TrackTargetingBehaviour<SignalBoundary> target = blockEntity.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = blockEntity.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock trackBlock) || overlayState == OverlayState.SKIP) {
            previousOverlayState = null;
            signalOverlay.setZeroTransform().setChanged();
            return;
        }

        if (overlayState != previousOverlayState) {
            previousOverlayState = overlayState;

            PartialModel partial;
            RenderedTrackOverlayType type;
            if (overlayState == OverlayState.DUAL) {
                type = RenderedTrackOverlayType.DUAL_SIGNAL;
                partial = AllPartialModels.TRACK_SIGNAL_DUAL_OVERLAY;
            } else {
                type = RenderedTrackOverlayType.SIGNAL;
                partial = AllPartialModels.TRACK_SIGNAL_OVERLAY;
            }

            instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.chunkPartial(partial))
                .stealInstance(signalOverlay);

            signalOverlay.setIdentityTransform().translate(targetPosition.subtract(renderOrigin()));

            TrackBlockRenderer renderer = AllTrackRenders.get(trackBlock);
            if (renderer != null) {
                renderer.prepareTrackOverlay(
                    signalOverlay,
                    level,
                    targetPosition,
                    trackState,
                    target.getTargetBezier(),
                    target.getTargetDirection(),
                    type
                );
            }

            signalOverlay.setChanged();
        }
    }
}
