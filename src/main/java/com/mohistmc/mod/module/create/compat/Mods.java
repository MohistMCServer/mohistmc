package com.mohistmc.mod.module.create.compat;

import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
    JEI, RRV, COMPUTERCRAFT,
    TRINKETS_UPDATED, PACKETFIXER, MODERNUI;

    private final String id;
    private final boolean loaded;

    Mods() {
        id = name().toLowerCase(Locale.ROOT);
        loaded = ModList.get().isLoaded(id);
    }

    /**
     * @return the mod id
     */
    public String id() {
        return id;
    }

    public Identifier identifier(String name) {
        return Identifier.fromNamespaceAndPath(id, name);
    }

    public Block getBlock(String id) {
        return BuiltInRegistries.BLOCK.getValue(identifier(id));
    }

    public Item getItem(String id) {
        return BuiltInRegistries.ITEM.getValue(identifier(id));
    }

    /**
     * @return a boolean of whether the mod is loaded or not based on mod id
     */
    public boolean isLoaded() {
        return loaded;
    }
}
