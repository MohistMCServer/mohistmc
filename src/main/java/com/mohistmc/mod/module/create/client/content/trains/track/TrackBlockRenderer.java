package com.mohistmc.mod.module.create.client.content.trains.track;

import com.mohistmc.mod.module.flywheel.lib.transform.Affine;
import com.mohistmc.mod.module.create.content.trains.station.StationBlockEntity;
import com.mohistmc.mod.module.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.mohistmc.mod.module.create.infrastructure.component.BezierTrackPointLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface TrackBlockRenderer {
    <Self extends Affine<Self>> void prepareTrackOverlay(
        Affine<Self> affine,
        BlockGetter world,
        BlockPos pos,
        BlockState state,
        BezierTrackPointLocation bezierPoint,
        AxisDirection direction,
        RenderedTrackOverlayType type
    );

    @Nullable TrackBlockRenderState getAssemblyRenderState(
        StationBlockEntity be,
        Vec3 offset,
        Level world,
        BlockPos pos,
        BlockState state
    );

    @Nullable TrackBlockRenderState getRenderState(
        Level world,
        Vec3 offset,
        BlockState trackState,
        BlockPos pos,
        AxisDirection direction,
        @Nullable BezierTrackPointLocation bezier,
        RenderedTrackOverlayType type,
        float scale
    );
}
