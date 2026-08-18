package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.audio;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.BehaviourType;

abstract class AudioBehaviour<T extends SmartBlockEntity> extends BlockEntityBehaviour<T> {
    public static final BehaviourType<AudioBehaviour<?>> TYPE = new BehaviourType<>();

    public AudioBehaviour(T be) {
        super(be);
    }

    @Override
    public void tick() {
        tickAudio();
    }

    public abstract void tickAudio();

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
