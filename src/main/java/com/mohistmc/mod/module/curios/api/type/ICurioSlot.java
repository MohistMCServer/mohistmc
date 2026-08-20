package com.mohistmc.mod.module.curios.api.type;

import com.mohistmc.mod.module.curios.api.SlotContext;
import com.mohistmc.mod.module.curios.api.extensions.ICurioSlotExtension;

/**
 * Representation of a curio slot in menus and user interfaces
 */
public interface ICurioSlot {

    default String getId() {
        return this.getSlotContext().identifier();
    }

    ICurioSlotExtension getSlotExtension();

    SlotContext getSlotContext();
}
