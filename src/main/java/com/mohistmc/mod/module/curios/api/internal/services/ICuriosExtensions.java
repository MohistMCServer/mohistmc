package com.mohistmc.mod.module.curios.api.internal.services;

import com.mohistmc.mod.module.curios.api.extensions.ICurioSlotExtension;
import com.mohistmc.mod.module.curios.api.type.capability.ICurioItem;
import javax.annotation.Nullable;
import net.minecraft.world.item.Item;

public interface ICuriosExtensions {

    void registerCurioItem(ICurioItem curio, Item... item);

    @Nullable
    ICurioItem getCurioItem(Item item);

    void registerSlotExtension(ICurioSlotExtension slotExtension, String... id);

    @Nullable
    ICurioSlotExtension getSlotExtension(String id);
}
