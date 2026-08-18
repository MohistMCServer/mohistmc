package com.mohistmc.mod.module.create.api.entity;

import com.mohistmc.mod.module.create.infrastructure.player.FakePlayerEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.FakePlayer;

public interface FakePlayerHandler {
    boolean FABRIC = true;

    static boolean has(Entity player) {
        if (player instanceof FakePlayer) {
            return true;
        }
        return player instanceof FakePlayerEntity;
    }
}
