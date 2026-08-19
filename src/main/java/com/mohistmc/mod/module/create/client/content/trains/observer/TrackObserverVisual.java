package com.mohistmc.mod.module.create.client.content.trains.observer;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.AllTrackRenders;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackBlockRenderer;
import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.visual.ShaderLightVisual;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleTickableVisual;
import com.mohistmc.mod.module.create.content.trains.observer.TrackObserver;
import com.mohistmc.mod.module.create.content.trains.observer.TrackObserverBlockEntity;
import com.mohistmc.mod.module.create.content.trains.track.ITrackBlock;
import com.mohistmc.mod.module.create.content.trains.track.TrackTargetingBehaviour;
import com.mohistmc.mod.module.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TrackObserverVisual extends AbstractBlockEntityVisual<TrackObserverBlockEntity> implements SimpleTickableVisual, ShaderLightVisual {
    private final TransformedInstance overlay;
    private @Nullable BlockPos oldTargetPos;

    public TrackObserverVisual(VisualizationContext ctx, TrackObserverBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        overlay = ctx.instancerProvider()
            .instancer(InstanceTypes.TRANSFORMED, Models.chunkPartial(AllPartialModels.TRACK_OBSERVER_OVERLAY))
            .createInstance();

        setupVisual();
    }

    @Override
    public void setSectionCollector(SectionCollector sectionCollector) {
        if (oldTargetPos != null) {
            lightSections = sectionCollector;
            LongSet longSet = new LongArraySet();
            longSet.add(SectionPos.asLong(pos));
            longSet.add(SectionPos.asLong(oldTargetPos));
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
        relight(overlay);
    }

    @Override
    protected void _delete() {
        overlay.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(overlay);
    }

    private void setupVisual() {
        TrackTargetingBehaviour<TrackObserver> target = blockEntity.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = blockEntity.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock trackBlock)) {
            overlay.setZeroTransform().setChanged();
            return;
        }

        if (!targetPosition.equals(oldTargetPos)) {
            oldTargetPos = targetPosition;

            overlay.setIdentityTransform().translate(targetPosition.subtract(renderOrigin()));

            TrackBlockRenderer renderer = AllTrackRenders.get(trackBlock);
            if (renderer != null) {
                RenderedTrackOverlayType type = RenderedTrackOverlayType.OBSERVER;
                renderer.prepareTrackOverlay(
                    overlay,
                    level,
                    targetPosition,
                    trackState,
                    target.getTargetBezier(),
                    target.getTargetDirection(),
                    type
                );
            }

            overlay.setChanged();
        }
    }
}
