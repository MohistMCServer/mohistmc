package com.mohistmc.mod.module.create.api.registry;

import com.mohistmc.mod.module.create.api.behaviour.display.DisplaySource;
import com.mohistmc.mod.module.create.api.behaviour.display.DisplayTarget;
import com.mohistmc.mod.module.create.api.contraption.ContraptionType;
import com.mohistmc.mod.module.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.mohistmc.mod.module.create.api.contraption.storage.item.MountedItemStorageType;
import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.mohistmc.mod.module.create.content.kinetics.fan.processing.FanProcessingType;
import com.mohistmc.mod.module.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.mohistmc.mod.module.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.mohistmc.mod.module.create.content.logistics.packagePort.PackagePortTargetType;
import com.mohistmc.mod.module.create.foundation.gui.menu.MenuType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CreateRegistries {
    public static final Registry<ArmInteractionPointType> ARM_INTERACTION_POINT_TYPE = create(CreateRegistryKeys.ARM_INTERACTION_POINT_TYPE);
    public static final Registry<FanProcessingType> FAN_PROCESSING_TYPE = create(CreateRegistryKeys.FAN_PROCESSING_TYPE);
    public static final Registry<ItemAttributeType> ITEM_ATTRIBUTE_TYPE = create(CreateRegistryKeys.ITEM_ATTRIBUTE_TYPE);
    public static final Registry<DisplaySource> DISPLAY_SOURCE = create(CreateRegistryKeys.DISPLAY_SOURCE);
    public static final Registry<DisplayTarget> DISPLAY_TARGET = create(CreateRegistryKeys.DISPLAY_TARGET);
    public static final Registry<MountedItemStorageType<?>> MOUNTED_ITEM_STORAGE_TYPE = createIntrusive(
        CreateRegistryKeys.MOUNTED_ITEM_STORAGE_TYPE);
    public static final Registry<MountedFluidStorageType<?>> MOUNTED_FLUID_STORAGE_TYPE = create(CreateRegistryKeys.MOUNTED_FLUID_STORAGE_TYPE);
    public static final Registry<ContraptionType> CONTRAPTION_TYPE = createIntrusive(CreateRegistryKeys.CONTRAPTION_TYPE);
    public static final Registry<MapCodec<? extends PotatoProjectileRenderMode>> POTATO_PROJECTILE_RENDER_MODE = create(
        CreateRegistryKeys.POTATO_PROJECTILE_RENDER_MODE);
    public static final Registry<MapCodec<? extends PotatoProjectileEntityHitAction>> POTATO_PROJECTILE_ENTITY_HIT_ACTION = create(
        CreateRegistryKeys.POTATO_PROJECTILE_ENTITY_HIT_ACTION);
    public static final Registry<MapCodec<? extends PotatoProjectileBlockHitAction>> POTATO_PROJECTILE_BLOCK_HIT_ACTION = create(
        CreateRegistryKeys.POTATO_PROJECTILE_BLOCK_HIT_ACTION);
    public static final Registry<PackagePortTargetType> PACKAGE_PORT_TARGET_TYPE = create(CreateRegistryKeys.PACKAGE_PORT_TARGET_TYPE);
    public static final Registry<MenuType<?>> MENU_TYPE = create(CreateRegistryKeys.MENU_TYPE);

    private static <T> Registry<T> create(ResourceKey<? extends Registry<T>> key) {
        return new RegistryBuilder<T>(key).sync(true).create();
    }

    private static <T> Registry<T> createIntrusive(ResourceKey<? extends Registry<T>> key) {
        return new RegistryBuilder<T>(key).sync(true).withIntrusiveHolders().create();
    }

    // Registers the built-in registries on the NewRegistryEvent (mod event bus).
    // RegistryBuilder.create() only constructs the registry; NewRegistryEvent.fill() then
    // registers it to BuiltInRegistries within an unfreeze window, so this is safe to run
    // even while the built-in registries are frozen.
    public static void register(NewRegistryEvent event) {
        event.register(ARM_INTERACTION_POINT_TYPE);
        event.register(FAN_PROCESSING_TYPE);
        event.register(ITEM_ATTRIBUTE_TYPE);
        event.register(DISPLAY_SOURCE);
        event.register(DISPLAY_TARGET);
        event.register(MOUNTED_ITEM_STORAGE_TYPE);
        event.register(MOUNTED_FLUID_STORAGE_TYPE);
        event.register(CONTRAPTION_TYPE);
        event.register(POTATO_PROJECTILE_RENDER_MODE);
        event.register(POTATO_PROJECTILE_ENTITY_HIT_ACTION);
        event.register(POTATO_PROJECTILE_BLOCK_HIT_ACTION);
        event.register(PACKAGE_PORT_TARGET_TYPE);
        event.register(MENU_TYPE);
    }
}