package com.mohistmc.mod.module.create.infrastructure.packet.s2c;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.AllPackets;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record ContraptionSeatMappingPacket(int entityId, Map<UUID, Integer> mapping,
                                           int dismountedId) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<ByteBuf, ContraptionSeatMappingPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ContraptionSeatMappingPacket::entityId,
        ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ByteBufCodecs.INT),
        ContraptionSeatMappingPacket::mapping,
        ByteBufCodecs.INT,
        ContraptionSeatMappingPacket::dismountedId,
        ContraptionSeatMappingPacket::new
    );

    public ContraptionSeatMappingPacket {
        mapping = Map.copyOf(mapping);
    }

    public ContraptionSeatMappingPacket(int entityID, Map<UUID, Integer> mapping) {
        this(entityID, mapping, -1);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        AllClientHandle.INSTANCE.onContraptionSeatMapping(this);
    }

    @Override
    public PacketType<ContraptionSeatMappingPacket> type() {
        return AllPackets.CONTRAPTION_SEAT_MAPPING;
    }
}
