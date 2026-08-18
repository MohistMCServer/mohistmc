package com.mohistmc.mod.module.create.content.logistics.packager;

import com.mohistmc.mod.module.create.api.packager.InventoryIdentifier;
import net.minecraft.world.Container;
import org.jspecify.annotations.Nullable;

/**
 * An item inventory, possibly with an associated InventoryIdentifier.
 */
public record IdentifiedInventory(@Nullable InventoryIdentifier identifier, Container handler) {
}
