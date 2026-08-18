package com.mohistmc.mod.module.create.content.equipment.zapper;

import com.mohistmc.mod.module.create.infrastructure.component.PlacementPatterns;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface ConfigureZapperPacket extends Packet<ServerGamePacketListener> {
    InteractionHand hand();

    PlacementPatterns pattern();

    void configureZapper(ItemStack stack);
}
