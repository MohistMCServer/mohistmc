package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.mohistmc.mod.module.create.api.registry.CreateRegistryKeys;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryValidator;
import net.minecraft.resources.ResourceKey;

public class AllDynamicRegistries {
    public static final List<RegistryDataLoader.RegistryData<?>> ALL = new ArrayList<>();

    public static <T> void register(ResourceKey<Registry<T>> key, Codec<T> codec) {
        ALL.add(new RegistryDataLoader.RegistryData<>(key, codec, RegistryValidator.none()));
    }

    public static void register() {
        register(CreateRegistryKeys.POTATO_PROJECTILE_TYPE, PotatoCannonProjectileType.CODEC);
    }
}
