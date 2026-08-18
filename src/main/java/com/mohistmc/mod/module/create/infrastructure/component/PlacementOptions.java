package com.mohistmc.mod.module.create.infrastructure.component;

import com.mojang.serialization.Codec;
import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum PlacementOptions implements StringRepresentable {
    Merged, Attached, Inserted;

    public static final Codec<PlacementOptions> CODEC = StringRepresentable.fromEnum(PlacementOptions::values);
    public static final StreamCodec<ByteBuf, PlacementOptions> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(
        PlacementOptions.class);

    public final String translationKey;

    PlacementOptions() {
        translationKey = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return translationKey;
    }
}
