package com.mohistmc.mod.module.create.foundation.utility;

import com.mohistmc.mod.module.create.AllClientHandle;
import java.util.function.Supplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;

public final class GlobalRegistryAccess {
    private static final Supplier<@Nullable RegistryAccess> supplier;

    static {
        if (AllClientHandle.INSTANCE.isClient()) {
            supplier = () -> AllClientHandle.INSTANCE.getPlayer().registryAccess();
        } else {
            supplier = () -> {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server == null) {
                    return null;
                }
                return server.registryAccess();
            };
        }
    }

    @Nullable
    public static RegistryAccess get() {
        return supplier.get();
    }

    public static RegistryAccess getOrThrow() {
        RegistryAccess registryAccess = get();
        if (registryAccess == null) {
            throw new IllegalStateException("Could not get RegistryAccess");
        }
        return registryAccess;
    }
}
