package com.mohistmc.mod.module.create.infrastructure.component;

import com.mojang.serialization.Codec;
import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum ClipboardType implements StringRepresentable {
    EMPTY, WRITTEN, EDITING;

    public static final Codec<ClipboardType> CODEC = StringRepresentable.fromEnum(ClipboardType::values);
    public static final StreamCodec<ByteBuf, ClipboardType> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(
        ClipboardType.class);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
