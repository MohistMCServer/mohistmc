package com.mohistmc.mod.module.create.infrastructure.packet.s2c;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.foundation.codec.CreateStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;

public record OpenScreenPacket(int id, Identifier menu, Component name,
                               byte[] data) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.CONTAINER_ID,
        OpenScreenPacket::id,
        Identifier.STREAM_CODEC,
        OpenScreenPacket::menu,
        ComponentSerialization.TRUSTED_STREAM_CODEC,
        OpenScreenPacket::name,
        CreateStreamCodecs.UNBOUNDED_BYTE_ARRAY,
        OpenScreenPacket::data,
        OpenScreenPacket::new
    );

    @Override
    public void handle(ClientGamePacketListener listener) {
        AllClientHandle.INSTANCE.onOpenScreen(listener, this);
    }

    @Override
    public PacketType<OpenScreenPacket> type() {
        return AllPackets.OPEN_SCREEN;
    }
}
