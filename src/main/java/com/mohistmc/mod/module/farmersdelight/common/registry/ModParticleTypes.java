package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes
{
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, FarmersDelight.MODID);

	public static final Supplier<SimpleParticleType> STAR = PARTICLE_TYPES.register("star",
			() -> new SimpleParticleType(true));
	public static final Supplier<SimpleParticleType> STEAM = PARTICLE_TYPES.register("steam",
			() -> new SimpleParticleType(true));
	public static final Supplier<SimpleParticleType> SPARKLE = PARTICLE_TYPES.register("sparkle",
			() -> new SimpleParticleType(true));
}
