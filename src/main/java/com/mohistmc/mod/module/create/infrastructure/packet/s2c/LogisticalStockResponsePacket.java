package com.mohistmc.mod.module.create.infrastructure.packet.s2c;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.catnip.codecs.stream.CatnipStreamCodecBuilders;
import com.mohistmc.mod.module.create.content.logistics.BigItemStack;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public record LogisticalStockResponsePacket(boolean lastPacket, BlockPos pos,
                                            List<BigItemStack> items) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, LogisticalStockResponsePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        LogisticalStockResponsePacket::lastPacket,
        BlockPos.STREAM_CODEC,
        LogisticalStockResponsePacket::pos,
        CatnipStreamCodecBuilders.list(BigItemStack.STREAM_CODEC),
        LogisticalStockResponsePacket::items,
        LogisticalStockResponsePacket::new
    );

    @Override
    public void handle(ClientGamePacketListener listener) {
        AllClientHandle.INSTANCE.onLogisticalStockResponse(listener, this);
    }

    @Override
    public PacketType<LogisticalStockResponsePacket> type() {
        return AllPackets.LOGISTICS_STOCK_RESPONSE;
    }
}
