package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.tooltip;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.BehaviourType;

public abstract class TooltipBehaviour<T extends SmartBlockEntity> extends BlockEntityBehaviour<T> {
    public static final BehaviourType<TooltipBehaviour<?>> TYPE = new BehaviourType<>();

    public TooltipBehaviour(T be) {
        super(be);
    }

    @Override
    public void tick() {
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
