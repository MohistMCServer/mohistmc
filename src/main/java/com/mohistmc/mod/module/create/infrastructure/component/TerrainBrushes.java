package com.mohistmc.mod.module.create.infrastructure.component;

import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import com.mohistmc.mod.module.create.content.equipment.zapper.terrainzapper.Brush;
import com.mohistmc.mod.module.create.content.equipment.zapper.terrainzapper.CuboidBrush;
import com.mohistmc.mod.module.create.content.equipment.zapper.terrainzapper.CylinderBrush;
import com.mohistmc.mod.module.create.content.equipment.zapper.terrainzapper.DynamicBrush;
import com.mohistmc.mod.module.create.content.equipment.zapper.terrainzapper.SphereBrush;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum TerrainBrushes implements StringRepresentable {
    Cuboid(new CuboidBrush()),
    Sphere(new SphereBrush()),
    Cylinder(new CylinderBrush()),
    Surface(new DynamicBrush(true)),
    Cluster(new DynamicBrush(false));

    public static final Codec<TerrainBrushes> CODEC = StringRepresentable.fromEnum(TerrainBrushes::values);
    public static final StreamCodec<ByteBuf, TerrainBrushes> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(
        TerrainBrushes.class);

    private final Brush brush;

    TerrainBrushes(Brush brush) {
        this.brush = brush;
    }

    public Brush get() {
        return brush;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
