package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.world.configuration.WildCropConfiguration;
import com.mohistmc.mod.module.farmersdelight.common.world.configuration.WildRiceConfiguration;
import com.mohistmc.mod.module.farmersdelight.common.world.feature.WildCropFeature;
import com.mohistmc.mod.module.farmersdelight.common.world.feature.WildRiceFeature;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBiomeFeatures
{
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, FarmersDelight.MODID);

	public static final Supplier<Feature<WildRiceConfiguration>> WILD_RICE = FEATURES.register("wild_rice", () -> new WildRiceFeature(WildRiceConfiguration.CODEC));
	public static final Supplier<Feature<WildCropConfiguration>> WILD_CROP = FEATURES.register("wild_crop", () -> new WildCropFeature(WildCropConfiguration.CODEC));
}
