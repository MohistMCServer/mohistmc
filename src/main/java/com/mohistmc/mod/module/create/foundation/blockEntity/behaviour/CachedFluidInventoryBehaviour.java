package com.mohistmc.mod.module.create.foundation.blockEntity.behaviour;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.infrastructure.fluids.FluidInventory;
import com.mohistmc.mod.module.create.infrastructure.fluids.SidedFluidInventory;
import com.mohistmc.mod.module.create.infrastructure.transfer.NeoFluidResourceHandler;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class CachedFluidInventoryBehaviour<T extends SmartBlockEntity> extends BlockEntityBehaviour<T> {
    public static final BehaviourType<CachedFluidInventoryBehaviour<?>> TYPE = new BehaviourType<>();
    private final Function<T, @Nullable FluidInventory> factory;
    private Function<@Nullable Direction, @Nullable ResourceHandler<FluidResource>> getter;

    public CachedFluidInventoryBehaviour(T be, Function<T, @Nullable FluidInventory> factory) {
        super(be);
        this.factory = factory;
        reset();
    }

    public static @Nullable <T extends SmartBlockEntity> ResourceHandler<FluidResource> get(
        T be,
        @Nullable Direction side
    ) {
        return be.getBehaviour(TYPE).get(side);
    }

    @Nullable
    public ResourceHandler<FluidResource> get(@Nullable Direction side) {
        return getter.apply(side);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private ResourceHandler<FluidResource> firstGet(@Nullable Direction direction) {
        FluidInventory inventory = factory.apply(blockEntity);
        if (inventory == null) {
            return null;
        }
        ResourceHandler<FluidResource> storage = new NeoFluidResourceHandler(inventory, null);
        if (inventory instanceof SidedFluidInventory) {
            @Nullable ResourceHandler<FluidResource>[] sides = new ResourceHandler[6];
            getter = side -> {
                if (side == null) {
                    return storage;
                }
                int i = side.get3DDataValue();
                ResourceHandler<FluidResource> sideStorage = sides[i];
                if (sideStorage == null) {
                    sideStorage = sides[i] = new NeoFluidResourceHandler(inventory, side);
                }
                return sideStorage;
            };
        } else {
            getter = ignored -> storage;
        }
        return getter.apply(direction);
    }

    public void reset() {
        getter = this::firstGet;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
