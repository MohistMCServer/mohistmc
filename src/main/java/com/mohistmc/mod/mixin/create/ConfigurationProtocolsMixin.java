package com.mohistmc.mod.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mohistmc.mod.module.create.AllPackets;
import java.util.function.Consumer;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.SimpleUnboundProtocol;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConfigurationProtocols.class)
public class ConfigurationProtocolsMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/ProtocolInfoBuilder;clientboundProtocol(Lnet/minecraft/network/ConnectionProtocol;Ljava/util/function/Consumer;)Lnet/minecraft/network/protocol/SimpleUnboundProtocol;"))
    private static SimpleUnboundProtocol<ClientConfigurationPacketListener, RegistryFriendlyByteBuf> addS2CPacket(
        ConnectionProtocol type,
        Consumer<ProtocolInfoBuilder<ClientConfigurationPacketListener, RegistryFriendlyByteBuf, Unit>> registrar,
        Operation<SimpleUnboundProtocol<ClientConfigurationPacketListener, RegistryFriendlyByteBuf>> original
    ) {
        return original.call(
            type,
            (Consumer<ProtocolInfoBuilder<ClientConfigurationPacketListener, RegistryFriendlyByteBuf, Unit>>) builder -> {
                registrar.accept(builder);
                AllPackets.S2C_CONFIG.forEach(builder::addPacket);
            }
        );
    }
}
