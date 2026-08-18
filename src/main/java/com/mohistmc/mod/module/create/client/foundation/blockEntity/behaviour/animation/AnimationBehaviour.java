package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.animation;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.BehaviourType;

public abstract class AnimationBehaviour<T extends SmartBlockEntity> extends BlockEntityBehaviour<T> {
    public static final BehaviourType<AnimationBehaviour<?>> TYPE = new BehaviourType<>();

    public AnimationBehaviour(T be) {
        super(be);
    }

    @Override
    public void tick() {
        tickAnimation();
    }

    public abstract void tickAnimation();

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
