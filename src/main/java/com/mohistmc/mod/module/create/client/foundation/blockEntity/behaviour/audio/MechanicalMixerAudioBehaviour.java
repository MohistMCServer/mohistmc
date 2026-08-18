package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.audio;

import com.mohistmc.mod.module.create.AllSoundEvents;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.content.kinetics.mixer.MechanicalMixerBlockEntity;

public class MechanicalMixerAudioBehaviour extends KineticAudioBehaviour<MechanicalMixerBlockEntity> {
    public MechanicalMixerAudioBehaviour(MechanicalMixerBlockEntity be) {
        super(be);
    }

    @Override
    public void tickAudio() {
        super.tickAudio();

        // SoundEvents.BLOCK_STONE_BREAK
        boolean slow = Math.abs(blockEntity.getSpeed()) < 65;
        if (slow && AnimationTickHolder.getTicks() % 2 == 0) {
            return;
        }
        if (blockEntity.runningTicks == 20) {
            AllSoundEvents.MIXING.playAt(blockEntity.getLevel(), blockEntity.getBlockPos(), 0.75f, 1, true);
        }
    }
}
