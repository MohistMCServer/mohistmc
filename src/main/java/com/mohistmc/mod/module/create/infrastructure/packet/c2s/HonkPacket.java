package com.mohistmc.mod.module.create.infrastructure.packet.c2s;

import com.mohistmc.mod.module.create.AllHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.content.trains.entity.Train;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record HonkPacket(UUID trainId, boolean isHonk) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, HonkPacket> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        HonkPacket::trainId,
        ByteBufCodecs.BOOL,
        HonkPacket::isHonk,
        HonkPacket::new
    );

    public HonkPacket(Train train, boolean isHonk) {
        this(train.id, isHonk);
    }

    @Override
    public void handle(ServerGamePacketListener listener) {
        AllHandle.onTrainHonk((ServerGamePacketListenerImpl) listener, this);
    }

    @Override
    public PacketType<HonkPacket> type() {
        return AllPackets.C_TRAIN_HONK;
    }
}
