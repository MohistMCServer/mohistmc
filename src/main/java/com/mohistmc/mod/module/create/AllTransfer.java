package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.kinetics.belt.BeltBlock;
import com.mohistmc.mod.module.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.CachedDirectionInventoryBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.CachedFluidInventoryBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.CachedInventoryBehaviour;
import com.mohistmc.mod.module.create.foundation.item.ItemHelper;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventory;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidItemInventory;
import com.mohistmc.mod.module.create.infrastructure.transfer.FluidInventoryWrapper;
import com.mohistmc.mod.module.create.infrastructure.transfer.FluidItemInventoryWrapper;
import com.mohistmc.mod.module.create.infrastructure.transfer.InventoryWrapper;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

/**
 * Loader-neutral Create inventories exposed through NeoForge 26.2 transfer
 * capabilities.
 */
public final class AllTransfer {
    private AllTransfer() {
    }

    @Nullable
    public static Supplier<@Nullable Container> getCacheInventory(
        ServerLevel world,
        BlockPos pos,
        Direction direction,
        @Nullable BiPredicate<@Nullable BlockEntity, Direction> filter
    ) {
        BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache =
            BlockCapabilityCache.create(Capabilities.Item.BLOCK, world, pos, direction);
        return () -> {
            ResourceHandler<ItemResource> handler = cache.getCapability();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (handler == null || filter != null && !filter.test(blockEntity, direction)) {
                return null;
            }
            return InventoryWrapper.of(handler);
        };
    }

    @Nullable
    public static Container getInventory(
        Level world,
        BlockPos pos,
        @Nullable BlockState state,
        @Nullable BlockEntity blockEntity,
        @Nullable Direction direction
    ) {
        ResourceHandler<ItemResource> handler =
            world.getCapability(Capabilities.Item.BLOCK, pos, state, blockEntity, direction);
        return handler == null ? null : InventoryWrapper.of(handler);
    }

    public static boolean hasFluidInventory(
        Level world,
        BlockPos pos,
        @Nullable BlockState state,
        @Nullable BlockEntity blockEntity,
        @Nullable Direction direction
    ) {
        return world.getCapability(Capabilities.Fluid.BLOCK, pos, state, blockEntity, direction) != null;
    }

    @Nullable
    public static Supplier<@Nullable FluidInventory> getCacheFluidInventory(
        ServerLevel world,
        BlockPos pos,
        Direction direction
    ) {
        BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> cache =
            BlockCapabilityCache.create(Capabilities.Fluid.BLOCK, world, pos, direction);
        return () -> {
            ResourceHandler<FluidResource> handler = cache.getCapability();
            return handler == null ? null : FluidInventoryWrapper.of(handler);
        };
    }

    @Nullable
    public static FluidInventory getFluidInventory(
        Level world,
        BlockPos pos,
        @Nullable BlockState state,
        @Nullable BlockEntity blockEntity,
        @Nullable Direction direction
    ) {
        ResourceHandler<FluidResource> handler =
            world.getCapability(Capabilities.Fluid.BLOCK, pos, state, blockEntity, direction);
        return handler == null ? null : FluidInventoryWrapper.of(handler);
    }

    public static boolean hasFluidInventory(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ItemAccess access = ItemAccess.forStack(stack);
        return access.getCapability(Capabilities.Fluid.ITEM) != null;
    }

    @Nullable
    public static FluidItemInventory getFluidInventory(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ItemAccess access = ItemAccess.forStack(stack);
        ResourceHandler<FluidResource> handler = access.getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : FluidItemInventoryWrapper.of(handler, access, stack);
    }

    private static ResourceHandler<ItemResource> wrap(Container inventory, @Nullable Direction side) {
        if (inventory instanceof WorldlyContainer worldly) {
            return new net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper(worldly, side);
        }
        return net.neoforged.neoforge.transfer.item.VanillaContainerWrapper.of(inventory);
    }

    private static <T extends SmartBlockEntity> void registerItemSide(
        RegisterCapabilitiesEvent event,
        BlockEntityType<T> type,
        Function<T, @Nullable Container> factory
    ) {
        BlockEntityBehaviour.add(type, (T be) -> new CachedInventoryBehaviour<>(be, factory));
        event.registerBlockEntity(Capabilities.Item.BLOCK, type, CachedInventoryBehaviour::get);
    }

    private static <T extends SmartBlockEntity> void registerItemSide(
        RegisterCapabilitiesEvent event,
        BlockEntityType<T> type,
        BiFunction<T, @Nullable Direction, @Nullable Container> factory
    ) {
        BlockEntityBehaviour.add(type, (T be) -> new CachedDirectionInventoryBehaviour<>(be, factory));
        event.registerBlockEntity(Capabilities.Item.BLOCK, type, CachedDirectionInventoryBehaviour::get);
    }

    private static <T extends SmartBlockEntity> void registerFluidSide(
        RegisterCapabilitiesEvent event,
        BlockEntityType<T> type,
        Function<T, @Nullable FluidInventory> factory
    ) {
        BlockEntityBehaviour.add(type, (T be) -> new CachedFluidInventoryBehaviour<>(be, factory));
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, CachedFluidInventoryBehaviour::get);
    }

    /**
     * Kept for the common bootstrap call. NeoForge capabilities themselves are
     * registered from {@link #registerCapabilities(RegisterCapabilitiesEvent)}.
     */
    public static void register() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerItemSide(event, AllBlockEntityTypes.DEPOT, be -> be.depotBehaviour.itemHandler);
        registerItemSide(event, AllBlockEntityTypes.WEIGHTED_EJECTOR, be -> be.depotBehaviour.itemHandler);
        registerItemSide(event, AllBlockEntityTypes.BELT, be -> {
            if (!BeltBlock.canTransportObjects(be.getBlockState())) {
                return null;
            }
            if (!be.isRemoved() && be.itemHandler == null) {
                be.initializeItemHandler();
            }
            return be.itemHandler;
        });
        registerItemSide(event, AllBlockEntityTypes.MILLSTONE, be -> be.capability);
        registerItemSide(event, AllBlockEntityTypes.SAW, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.BASIN, be -> be.itemCapability);
        registerItemSide(event, AllBlockEntityTypes.ANDESITE_TUNNEL, be -> {
            if (be.cap == null) {
                Level world = be.getLevel();
                BlockPos pos = be.getBlockPos();
                BlockState state = world.getBlockState(pos.below());
                if (state.is(AllBlocks.BELT)) {
                    BlockEntity beBelow = world.getBlockEntity(pos.below());
                    if (beBelow != null) {
                        Container capBelow =
                            ItemHelper.getInventory(world, pos.below(), state, beBelow, Direction.UP);
                        if (capBelow != null) {
                            be.cap = capBelow;
                        }
                    }
                }
            }
            return be.cap;
        });
        registerItemSide(event, AllBlockEntityTypes.BRASS_TUNNEL, be -> be.tunnelCapability);
        registerItemSide(event, AllBlockEntityTypes.CHUTE, be -> be.itemHandler);
        registerItemSide(event, AllBlockEntityTypes.SMART_CHUTE, be -> be.itemHandler);
        registerItemSide(event, AllBlockEntityTypes.PORTABLE_STORAGE_INTERFACE, be -> be.capability);
        registerItemSide(event, AllBlockEntityTypes.ITEM_DRAIN, (be, side) ->
            side != null && side.getAxis().isHorizontal() ? be.itemHandlers.get(side) : null);
        registerItemSide(event, AllBlockEntityTypes.DEPLOYER, be -> {
            if (be.invHandler == null) {
                be.initHandler();
            }
            return be.invHandler;
        });
        registerItemSide(event, AllBlockEntityTypes.CRUSHING_WHEEL_CONTROLLER, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.MECHANICAL_CRAFTER, MechanicalCrafterBlockEntity::getInvCapability);
        registerItemSide(event, AllBlockEntityTypes.CREATIVE_CRATE, be -> be.inv);
        registerItemSide(event, AllBlockEntityTypes.PACKAGER, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.REPACKAGER, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.PACKAGE_POSTBOX, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.PACKAGE_FROGPORT, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.TOOLBOX, be -> be.inventory);
        registerItemSide(event, AllBlockEntityTypes.TRACK_STATION, be -> be.depotBehaviour.itemHandler);

        registerFluidSide(event, AllBlockEntityTypes.FLUID_TANK, be -> {
            if (be.fluidCapability == null) {
                be.refreshCapability();
            }
            return be.fluidCapability;
        });
        registerFluidSide(event, AllBlockEntityTypes.BASIN, be -> be.fluidCapability);
        registerFluidSide(event, AllBlockEntityTypes.PORTABLE_FLUID_INTERFACE, be -> be.capability);
        registerFluidSide(event, AllBlockEntityTypes.HOSE_PULLEY, be -> be.handler);
        registerFluidSide(event, AllBlockEntityTypes.SPOUT, be -> be.tank.getCapability());
    }
}
