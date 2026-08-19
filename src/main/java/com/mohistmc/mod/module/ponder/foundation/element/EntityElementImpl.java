package com.mohistmc.mod.module.ponder.foundation.element;

import com.mohistmc.mod.module.ponder.api.element.EntityElement;
import net.minecraft.world.entity.Entity;

public class EntityElementImpl extends TrackedElementBase<Entity> implements EntityElement {

    public EntityElementImpl(Entity wrapped) {
        super(wrapped);
    }

    @Override
    public boolean isStillValid(Entity element) {
        return element.isAlive();
    }

}