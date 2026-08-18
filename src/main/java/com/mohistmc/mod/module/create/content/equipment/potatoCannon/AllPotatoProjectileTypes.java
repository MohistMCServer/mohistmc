package com.mohistmc.mod.module.create.content.equipment.potatoCannon;

import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.mohistmc.mod.module.create.api.registry.CreateRegistryKeys;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllPotatoProjectileTypes {
    public static final ResourceKey<PotatoCannonProjectileType> FALLBACK = ResourceKey.create(
        CreateRegistryKeys.POTATO_PROJECTILE_TYPE,
        Identifier.fromNamespaceAndPath(MOD_ID, "fallback")
    );
}
