package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.AllTrackMaterials;
import com.mohistmc.mod.module.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.create.content.trains.track.TrackMaterial;

public class AllTrackMaterialModels {
    static final TrackModelHolder ANDESITE = new TrackModelHolder(
        AllPartialModels.TRACK_TIE,
        AllPartialModels.TRACK_SEGMENT_LEFT,
        AllPartialModels.TRACK_SEGMENT_RIGHT
    );

    public record TrackModelHolder(PartialModel tie, PartialModel leftSegment, PartialModel rightSegment) {
    }

    public static void register(TrackMaterial material, TrackModelHolder holder) {
        material.modelHolder = holder;
    }

    public static void register() {
        register(AllTrackMaterials.ANDESITE, ANDESITE);
    }
}
