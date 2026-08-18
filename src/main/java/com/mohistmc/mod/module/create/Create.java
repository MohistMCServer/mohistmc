package com.mohistmc.mod.module.create;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mohistmc.mod.MohistMC;
import com.mojang.logging.LogUtils;
import com.mohistmc.mod.module.create.api.registry.CreateRegistries;
import com.mohistmc.mod.module.create.api.registry.CreateRegistryKeys;
import com.mohistmc.mod.module.create.api.stress.BlockStressValues;
import com.mohistmc.mod.module.create.compat.CompatMod;
import com.mohistmc.mod.module.create.content.decoration.encasing.EncasingRegistry;
import com.mohistmc.mod.module.create.content.equipment.armor.AllArmorMaterials;
import com.mohistmc.mod.module.create.content.equipment.armor.AllEquipmentAssetKeys;
import com.mohistmc.mod.module.create.content.equipment.potatoCannon.AllPotatoProjectileBlockHitActions;
import com.mohistmc.mod.module.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import com.mohistmc.mod.module.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.mohistmc.mod.module.create.content.equipment.tool.AllToolMaterials;
import com.mohistmc.mod.module.create.content.fluids.AllFlowCollision;
import com.mohistmc.mod.module.create.content.fluids.tank.BoilerHeaters;
import com.mohistmc.mod.module.create.content.kinetics.TorquePropagator;
import com.mohistmc.mod.module.create.content.kinetics.belt.BeltHelper;
import com.mohistmc.mod.module.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.mohistmc.mod.module.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.mohistmc.mod.module.create.content.logistics.packagePort.AllPackagePortTargetTypes;
import com.mohistmc.mod.module.create.content.logistics.packagerLink.GlobalLogisticsManager;
import com.mohistmc.mod.module.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.mohistmc.mod.module.create.content.schematics.ServerSchematicLoader;
import com.mohistmc.mod.module.create.content.trains.GlobalRailwayManager;
import com.mohistmc.mod.module.create.content.trains.bogey.AllBogeySizes;
import com.mohistmc.mod.module.create.content.trains.entity.CarriageEntityHandler;
import com.mohistmc.mod.module.create.content.trains.track.AllPortalTracks;
import com.mohistmc.mod.module.create.foundation.CreateNBTProcessors;
import com.mohistmc.mod.module.create.foundation.recipe.RecipeFinder;
import com.mohistmc.mod.module.create.foundation.recipe.trie.RecipeTrieFinder;
import com.mohistmc.mod.module.create.infrastructure.config.AllConfigs;
import com.mohistmc.mod.module.create.infrastructure.worldgen.AllConfiguredFeatures;
import com.mohistmc.mod.module.create.infrastructure.worldgen.AllFeatures;
import com.mohistmc.mod.module.create.infrastructure.worldgen.AllPlacementModifiers;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

public class Create {
    public static final String MOD_ID = MohistMC.MODID;
    public static final String NAME = "Create";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION = "6.0.10-port.1";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static boolean Lazy;

    public static TorquePropagator TORQUE_PROPAGATOR;
    public static GlobalRailwayManager RAILWAYS;
    public static RedstoneLinkNetworkHandler REDSTONE_LINK_NETWORK_HANDLER;
    public static GlobalLogisticsManager LOGISTICS;
    public static ServerSchematicLoader SCHEMATIC_RECEIVER;

    public Create(IEventBus modEventBus, ModContainer modContainer) {
        AllConfigs.register();
        modEventBus.addListener(AllTransfer::registerCapabilities);
        modEventBus.addListener(AllCreativeModeTabs::register);
        modEventBus.addListener(Create::registerDataPackRegistries);
        NeoForge.EVENT_BUS.addListener(Create::addServerReloadListeners);
        NeoForge.EVENT_BUS.addListener(Create::onEntityEnteringSection);
    }

    private static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
            CreateRegistryKeys.POTATO_PROJECTILE_TYPE,
            com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoCannonProjectileType.CODEC,
            com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoCannonProjectileType.CODEC
        );
    }

    private static void addServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "recipe_finder"), RecipeFinder.LISTENER);
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "recipe_trie_finder"), RecipeTrieFinder.LISTENER);
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "belt_helper"), BeltHelper.LISTENER);
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "configs"), AllConfigs.LISTENER);
    }

    private static void onEntityEnteringSection(EntityEvent.EnteringSection event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        CarriageEntityHandler.onEntityEnterSection(
            event.getEntity(),
            event.getPackedOldPos(),
            event.getPackedNewPos()
        );
    }

    public static void register() {
        TORQUE_PROPAGATOR = new TorquePropagator();
        RAILWAYS = new GlobalRailwayManager();
        REDSTONE_LINK_NETWORK_HANDLER = new RedstoneLinkNetworkHandler();
        LOGISTICS = new GlobalLogisticsManager();
        SCHEMATIC_RECEIVER = new ServerSchematicLoader();
        CreateRegistryKeys.register();
        CreateRegistries.register();
        AllPackageStyles.register();
        AllToolMaterials.register();
        AllArmorMaterials.register();
        EncasingRegistry.register();
        BlockStressValues.register();
        AllItemIds.register();
        AllItems.init();
        AllFlowCollision.register();
        AllFluidTags.register();
        AllBlockItemTags.register();
        AllBlockTags.register();
        AllItemTags.register();
        AllMountedItemStorageTypeTags.register();
        AllContraptionTypeTags.register();
        AllEntityTags.register();
        AllSoundEvents.register();
        AllParticleTypes.register();
        AllDataComponents.register();
        AllDamageTypes.register();
        AllPackets.register();
        AllContraptionTypes.register();
        AllEntityTypes.register();
        AllBlockEntityTypes.register();
        AllAdvancements.register();
        AllRecipeTypes.register();
        AllRecipeSerializers.register();
        AllRecipeSets.register();
        AllFluidItemInventory.register();
        AllTransfer.register();
        AllOpenPipeEffectHandlers.register();
        AllArmInteractionPointTypes.register();
        AllFanProcessingTypes.register();
        BoilerHeaters.register();
        AllSynchedDatas.register();
        AllMountedStorageTypes.register();
        AllMovementBehaviours.register();
        AllContraptionMovementSettings.register();
        AllInteractionBehaviours.register();
        AllEquipmentAssetKeys.register();
        AllTrackMaterials.register();
        AllDisplayTargets.register();
        AllDisplaySources.register();
        AllMapDecorationTypes.register();
        AllBogeySizes.register();
        AllBogeyStyles.register();
        AllPortalTracks.register();
        AllSchedules.register();
        AllMenuTypes.register();
        AllAssemblyRecipeNames.register();
        AllPotatoProjectileRenderModes.register();
        AllPotatoProjectileBlockHitActions.register();
        AllPotatoProjectileEntityHitActions.register();
        AllItemAttributeTypes.register();
        AllPackagePortTargetTypes.register();
        AllUnpackingHandlers.register();
        AllFuelTimes.register();
        AllStructureProcessorTypes.register();
        CreateNBTProcessors.register();
        AllFeatures.register();
        AllConfiguredFeatures.register();
        AllPlacementModifiers.register();
        AllMountedDispenseItemBehaviors.register();
        AllBlockSpoutingBehaviours.register();
        AllDataComponentPredicates.register();
        CompatMod.register();
    }
}
