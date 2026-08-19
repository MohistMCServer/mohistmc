package com.mohistmc.mod.module.farmersdelight;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.farmersdelight.common.CommonSetup;
import com.mohistmc.mod.module.farmersdelight.common.Configuration;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModAdvancements;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBiomeFeatures;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBiomeModifiers;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlockEntityTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlocks;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModConditionCodecs;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModCreativeTabs;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModDataComponents;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModEffects;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModEntityTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModIngredientTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModLootFunctions;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModLootModifiers;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModMenuTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModParticleTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModPlacementModifiers;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeSerializers;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModSounds;
import com.mohistmc.mod.module.farmersdelight.common.registry.RegistryAliases;
import com.mohistmc.mod.module.farmersdelight.common.world.VillageStructures;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FarmersDelight
{
	public static final String MODID = MohistMC.MODID;
	public static final Logger LOGGER = LogManager.getLogger();

	public static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MODID, name);
	}

	public FarmersDelight(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(CommonSetup::init);
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}

		modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
		modContainer.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);

		ModSounds.SOUNDS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModEffects.EFFECTS.register(modEventBus);
		ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModDataComponents.DATA_COMPONENTS.register(modEventBus);
		ModDataComponents.ENCHANTMENT_EFFECT_COMPONENTS.register(modEventBus);
		ModEntityTypes.ENTITIES.register(modEventBus);
		ModBlockEntityTypes.TILES.register(modEventBus);
		ModMenuTypes.MENU_TYPES.register(modEventBus);
		ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
		ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		ModBiomeFeatures.FEATURES.register(modEventBus);
		ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
		ModPlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
		ModBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
		ModLootFunctions.LOOT_FUNCTIONS.register(modEventBus);
		ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
		ModConditionCodecs.CONDITION_CODECS.register(modEventBus);
		ModIngredientTypes.INGREDIENT_TYPES.register(modEventBus);
		ModAdvancements.TRIGGERS.register(modEventBus);

		RegistryAliases.addRegistryAliases();

		NeoForge.EVENT_BUS.addListener(VillageStructures::addNewVillageBuilding);
	}
}
