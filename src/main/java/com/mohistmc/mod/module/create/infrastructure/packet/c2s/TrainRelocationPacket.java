package com.mohistmc.mod.module.create.infrastructure.packet.c2s;

import com.mohistmc.mod.module.create.AllHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import com.mohistmc.mod.module.create.infrastructure.component.BezierTrackPointLocation;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record TrainRelocationPacket(UUID trainId, BlockPos pos, Vec3 lookAngle, int entityId, boolean direction,
                                    @Nullable BezierTrackPointLocation hoveredBezier) implements Packet<ServerGamePacketListener> {
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, TrainRelocationPacket> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        TrainRelocationPacket::trainId,
        BlockPos.STREAM_CODEC,
        TrainRelocationPacket::pos,
        Vec3.STREAM_CODEC,
        TrainRelocationPacket::lookAngle,
        ByteBufCodecs.INT,
        TrainRelocationPacket::entityId,
        ByteBufCodecs.BOOL,
        TrainRelocationPacket::direction,
        CatnipStreamCodecBuilders.nullable(BezierTrackPointLocation.STREAM_CODEC),
        TrainRelocationPacket::hoveredBezier,
        TrainRelocationPacket::new
    );

    @Override
    public void handle(ServerGamePacketListener listener) {
        AllHandle.onTrainRelocation((ServerGamePacketListenerImpl) listener, this);
    }

    @Override
    public PacketType<TrainRelocationPacket> type() {
        return AllPackets.RELOCATE_TRAIN;
    }
}
