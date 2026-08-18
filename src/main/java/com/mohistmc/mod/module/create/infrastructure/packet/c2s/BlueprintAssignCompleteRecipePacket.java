package com.mohistmc.mod.module.create.infrastructure.packet.c2s;

import com.mohistmc.mod.module.create.AllHandle;
import com.mohistmc.mod.module.create.AllPackets;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public record BlueprintAssignCompleteRecipePacket(List<ItemStack> input,
                                                  ItemStack output) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintAssignCompleteRecipePacket> CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
        BlueprintAssignCompleteRecipePacket::input,
        ItemStack.STREAM_CODEC,
        BlueprintAssignCompleteRecipePacket::output,
        BlueprintAssignCompleteRecipePacket::new
    );

    @Override
    public void handle(ServerGamePacketListener listener) {
        AllHandle.onBlueprintAssignCompleteRecipe((ServerGamePacketListenerImpl) listener, this);
    }

    @Override
    public PacketType<BlueprintAssignCompleteRecipePacket> type() {
        return AllPackets.BLUEPRINT_COMPLETE_RECIPE;
    }
}
