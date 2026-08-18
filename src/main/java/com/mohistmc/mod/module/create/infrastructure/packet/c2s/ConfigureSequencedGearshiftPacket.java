package com.mohistmc.mod.module.create.infrastructure.packet.c2s;

import com.mohistmc.mod.module.create.AllHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.content.kinetics.transmission.sequencer.Instruction;
import com.mohistmc.mod.module.create.foundation.codec.CreateStreamCodecs;
import java.util.Vector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record ConfigureSequencedGearshiftPacket(BlockPos pos,
                                                Vector<Instruction> instructions) implements Packet<ServerGamePacketListener> {
    @SuppressWarnings("removal")
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigureSequencedGearshiftPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ConfigureSequencedGearshiftPacket::pos,
        Instruction.STREAM_CODEC.apply(CreateStreamCodecs.vector()),
        ConfigureSequencedGearshiftPacket::instructions,
        ConfigureSequencedGearshiftPacket::new
    );

    @Override
    public void handle(ServerGamePacketListener listener) {
        AllHandle.onConfigureSequencedGearshift((ServerGamePacketListenerImpl) listener, this);
    }

    @Override
    public PacketType<ConfigureSequencedGearshiftPacket> type() {
        return AllPackets.CONFIGURE_SEQUENCER;
    }
}
