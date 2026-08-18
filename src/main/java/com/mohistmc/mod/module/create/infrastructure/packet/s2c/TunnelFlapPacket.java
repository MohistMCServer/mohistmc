package com.mohistmc.mod.module.create.infrastructure.packet.s2c;

import com.mojang.datafixers.util.Pair;
import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import com.mohistmc.mod.module.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record TunnelFlapPacket(BlockPos pos,
                               List<Pair<Direction, Boolean>> flaps) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<ByteBuf, TunnelFlapPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        TunnelFlapPacket::pos,
        CatnipStreamCodecBuilders.list(CatnipStreamCodecBuilders.pair(Direction.STREAM_CODEC, ByteBufCodecs.BOOL)),
        TunnelFlapPacket::flaps,
        TunnelFlapPacket::new
    );

    public TunnelFlapPacket(BeltTunnelBlockEntity blockEntity, List<Pair<Direction, Boolean>> flaps) {
        this(blockEntity.getBlockPos(), new ArrayList<>(flaps));
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        AllClientHandle.INSTANCE.onTunnelFlap(this);
    }

    @Override
    public PacketType<TunnelFlapPacket> type() {
        return AllPackets.TUNNEL_FLAP;
    }
}
