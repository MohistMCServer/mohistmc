package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.AllBlocks;
import com.mohistmc.mod.module.create.client.content.trains.track.StandardTrackBlockRenderer;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackBlockRenderer;
import com.mohistmc.mod.module.create.content.trains.track.ITrackBlock;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class AllTrackRenders {
    public static final Map<ITrackBlock, TrackBlockRenderer> ALL = new IdentityHashMap<>();

    @Nullable
    public static TrackBlockRenderer get(ITrackBlock block) {
        return ALL.get(block);
    }

    public static void register(ITrackBlock block, Supplier<TrackBlockRenderer> factory) {
        ALL.put(block, factory.get());
    }

    public static void register() {
        register(AllBlocks.TRACK, StandardTrackBlockRenderer::new);
    }
}
