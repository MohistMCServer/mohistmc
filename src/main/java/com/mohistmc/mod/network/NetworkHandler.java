package com.mohistmc.mod.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * @author Mgazul
 * @date 2026/3/31 21:49
 */
@EventBusSubscriber
public class NetworkHandler {

    private static final String VERSION = "1.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
    }


    public static void sendToClientPlayer(CustomPacketPayload message, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, message);
    }

    public static void sendToTrackingEntity(CustomPacketPayload message, final Entity centerEntity) {
        PacketDistributor.sendToPlayersTrackingEntity(centerEntity, message);
    }
}
