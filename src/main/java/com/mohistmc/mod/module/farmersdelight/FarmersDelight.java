package com.mohistmc.mod.module.farmersdelight;

import com.mohistmc.mod.MohistMC;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mohistmc.mod.module.farmersdelight.common.CommonSetup;
import com.mohistmc.mod.module.farmersdelight.common.Configuration;
import com.mohistmc.mod.module.farmersdelight.common.registry.*;

public class FarmersDelight
{
	public static final String MODID = MohistMC.MODID;
	public static final Logger LOGGER = LogManager.getLogger();

	public static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MODID, name);
	}

	public FarmersDelight(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(CommonSetup::init);
		if (FMLEnvironment.getDist().isClient()) {
			// TODO may be unnecessary?
			//modEventBus.addListener(ClientSetupEvents::init);
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
		ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
		ModLootFunctions.LOOT_FUNCTIONS.register(modEventBus);
		ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
		ModConditionCodecs.CONDITION_CODECS.register(modEventBus);
		ModIngredientTypes.INGREDIENT_TYPES.register(modEventBus);
		ModConsumeEffectTypes.CONSUME_EFFECTS.register(modEventBus);
		ModRecipeCategories.RECIPE_BOOK_CATEGORIES.register(modEventBus);

		RegistryAliases.addRegistryAliases();
	}
}
