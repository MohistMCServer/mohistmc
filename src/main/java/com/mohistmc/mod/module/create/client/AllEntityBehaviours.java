package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.AllEntityTypes;
import com.mohistmc.mod.module.create.api.behaviour.EntityBehaviour;
import com.mohistmc.mod.module.create.client.foundation.entity.behaviour.CarriageAudioBehaviour;
import com.mohistmc.mod.module.create.client.foundation.entity.behaviour.CarriageParticleBehaviour;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class AllEntityBehaviours {
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T extends Entity> void add(EntityType<T> type, Function<T, EntityBehaviour<?>>... factories) {
        for (Function<T, EntityBehaviour<?>> factory : factories) {
            EntityBehaviour.CLIENT_REGISTRY.add(type, (Function<Entity, EntityBehaviour<?>>) factory);
        }
    }

    public static void register() {
        add(AllEntityTypes.CARRIAGE_CONTRAPTION, CarriageAudioBehaviour::new, CarriageParticleBehaviour::new);
    }
}
