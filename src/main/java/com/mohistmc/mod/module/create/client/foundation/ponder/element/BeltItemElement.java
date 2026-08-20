package com.mohistmc.mod.module.create.client.foundation.ponder.element;

import com.mohistmc.mod.module.create.content.kinetics.belt.transport.TransportedItemStack;
import com.mohistmc.mod.module.ponder.foundation.element.TrackedElementBase;

public class BeltItemElement extends TrackedElementBase<TransportedItemStack> {

    public BeltItemElement(TransportedItemStack wrapped) {
        super(wrapped);
    }

}