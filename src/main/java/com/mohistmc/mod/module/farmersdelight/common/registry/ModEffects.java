package com.mohistmc.mod.module.farmersdelight.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.effect.ComfortEffect;
import com.mohistmc.mod.module.farmersdelight.common.effect.NourishmentEffect;

public class ModEffects
{
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FarmersDelight.MODID);

	public static final Holder<MobEffect> NOURISHMENT = EFFECTS.register("nourishment", NourishmentEffect::new);
	// TODO: Remove Comfort.
	public static final Holder<MobEffect> COMFORT = EFFECTS.register("comfort", ComfortEffect::new);
}
