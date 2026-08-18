package com.mohistmc.mod.module.create.api.registry;

import com.mohistmc.mod.module.create.api.behaviour.display.DisplaySource;
import com.mohistmc.mod.module.create.api.behaviour.display.DisplayTarget;
import com.mohistmc.mod.module.create.api.contraption.ContraptionType;
import com.mohistmc.mod.module.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.mohistmc.mod.module.create.api.contraption.storage.item.MountedItemStorageType;
import com.mohistmc.mod.module.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class CreateRegistryKeys {
    public static final ResourceKey<Registry<ArmInteractionPointType>> ARM_INTERACTION_POINT_TYPE = register(
        "arm_interaction_point_type");
    public static final ResourceKey<Registry<FanProcessingType>> FAN_PROCESSING_TYPE = register("fan_processing_type");
    public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_TYPE = register("item_attribute_type");
    public static final ResourceKey<Registry<DisplaySource>> DISPLAY_SOURCE = register("display_source");
    public static final ResourceKey<Registry<DisplayTarget>> DISPLAY_TARGET = register("display_target");
    public static final ResourceKey<Registry<MountedItemStorageType<?>>> MOUNTED_ITEM_STORAGE_TYPE = register(
        "mounted_item_storage_type");
    public static final ResourceKey<Registry<MountedFluidStorageType<?>>> MOUNTED_FLUID_STORAGE_TYPE = register(
        "mounted_fluid_storage_type");
    public static final ResourceKey<Registry<ContraptionType>> CONTRAPTION_TYPE = register("contraption_type");
    public static final ResourceKey<Registry<PotatoCannonProjectileType>> POTATO_PROJECTILE_TYPE = register(
        "potato_projectile/type");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileRenderMode>>> POTATO_PROJECTILE_RENDER_MODE = register(
        "potato_projectile/render_mode");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileEntityHitAction>>> POTATO_PROJECTILE_ENTITY_HIT_ACTION = register(
        "potato_projectile/entity_hit_action");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileBlockHitAction>>> POTATO_PROJECTILE_BLOCK_HIT_ACTION = register(
        "potato_projectile/block_hit_action");
    public static final ResourceKey<Registry<PackagePortTargetType>> PACKAGE_PORT_TARGET_TYPE = register(
        "package_port_target_type");
    public static final ResourceKey<Registry<MenuType<?>>> MENU_TYPE = register("menu_type");

    private static <T> ResourceKey<Registry<T>> register(String id) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID, id));
    }

    public static void register() {
    }
}
