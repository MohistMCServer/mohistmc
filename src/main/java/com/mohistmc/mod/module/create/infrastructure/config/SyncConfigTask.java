package com.mohistmc.mod.module.create.infrastructure.config;

import com.mohistmc.mod.module.create.infrastructure.packet.s2c.ServerConfigPacket;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public record SyncConfigTask(Consumer<Type> callback) implements ConfigurationTask {
    public static Type KEY = new Type(MOD_ID);

    @Override
    public void start(Consumer<Packet<?>> sender) {
        Packet<?> packet = ServerConfigPacket.create();
        if (packet != null) {
            sender.accept(packet);
        }
        callback.accept(KEY);
    }

    @Override
    public Type type() {
        return KEY;
    }
}
