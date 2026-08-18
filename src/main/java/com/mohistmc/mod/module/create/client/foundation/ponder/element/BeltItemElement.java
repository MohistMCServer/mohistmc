package com.mohistmc.mod.module.create.client.foundation.ponder.element;

import com.mohistmc.mod.module.create.client.ponder.foundation.element.TrackedElementBase;
import com.mohistmc.mod.module.create.content.kinetics.belt.transport.TransportedItemStack;

public class BeltItemElement extends TrackedElementBase<TransportedItemStack> {

    public BeltItemElement(TransportedItemStack wrapped) {
        super(wrapped);
    }

}