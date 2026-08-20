package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.client.infrastructure.particle.AirFlowParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.AirParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.BasicParticleFactory;
import com.mohistmc.mod.module.create.client.infrastructure.particle.BasinFluidParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.CubeParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.FluidParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.RotationIndicatorParticle;
import com.mohistmc.mod.module.create.client.infrastructure.particle.SteamJetParticle;
import net.minecraft.client.particle.ParticleResources;

import static com.mohistmc.mod.module.create.AllParticleTypes.AIR;
import static com.mohistmc.mod.module.create.AllParticleTypes.AIR_FLOW;
import static com.mohistmc.mod.module.create.AllParticleTypes.BASIN_FLUID;
import static com.mohistmc.mod.module.create.AllParticleTypes.CUBE;
import static com.mohistmc.mod.module.create.AllParticleTypes.FLUID_PARTICLE;
import static com.mohistmc.mod.module.create.AllParticleTypes.ROTATION_INDICATOR;
import static com.mohistmc.mod.module.create.AllParticleTypes.SOUL;
import static com.mohistmc.mod.module.create.AllParticleTypes.SOUL_BASE;
import static com.mohistmc.mod.module.create.AllParticleTypes.SOUL_EXPANDING_PERIMETER;
import static com.mohistmc.mod.module.create.AllParticleTypes.SOUL_PERIMETER;
import static com.mohistmc.mod.module.create.AllParticleTypes.STEAM_JET;
import static com.mohistmc.mod.module.create.AllParticleTypes.WIFI;

public class AllParticleTypes {
    public static void register(ParticleResources particle) {
        particle.register(ROTATION_INDICATOR, RotationIndicatorParticle.Factory::new);
        particle.register(AIR_FLOW, AirFlowParticle.Factory::new);
        particle.register(AIR, AirParticle.Factory::new);
        particle.register(STEAM_JET, SteamJetParticle.Factory::new);
        particle.register(CUBE, new CubeParticle.Factory());
        particle.register(FLUID_PARTICLE, new FluidParticle.Factory());
        particle.register(BASIN_FLUID, new BasinFluidParticle.Factory());
        particle.register(WIFI, BasicParticleFactory::wifi);
        particle.register(SOUL, BasicParticleFactory::soul);
        particle.register(SOUL_BASE, BasicParticleFactory::soulBase);
        particle.register(SOUL_PERIMETER, BasicParticleFactory::soul);
        particle.register(SOUL_EXPANDING_PERIMETER, BasicParticleFactory::soul);
    }
}
