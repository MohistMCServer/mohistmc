package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.Identifier;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllTrackMaterials {
    public static final TrackMaterial ANDESITE = new TrackMaterial(
        Identifier.fromNamespaceAndPath(MOD_ID, "andesite"),
        () -> AllBlocks.TRACK
    );

    public static void register() {
    }
}
