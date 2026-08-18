package com.mohistmc.mod.module.create.foundation.blockEntity.behaviour;

import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import org.jspecify.annotations.Nullable;

public class CachedInventoryBehaviour<T extends SmartBlockEntity> extends BlockEntityBehaviour<T> {
    public static final BehaviourType<CachedInventoryBehaviour<?>> TYPE = new BehaviourType<>();
    private final Function<T, @Nullable Container> factory;
    private Function<@Nullable Direction, @Nullable ResourceHandler<ItemResource>> getter;

    public CachedInventoryBehaviour(T be, Function<T, @Nullable Container> factory) {
        super(be);
        this.factory = factory;
        reset();
    }

    public static @Nullable <T extends SmartBlockEntity> ResourceHandler<ItemResource> get(
        T be,
        @Nullable Direction side
    ) {
        return be.getBehaviour(TYPE).get(side);
    }

    @Nullable
    public ResourceHandler<ItemResource> get(@Nullable Direction side) {
        return getter.apply(side);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private ResourceHandler<ItemResource> firstGet(@Nullable Direction direction) {
        Container inventory = factory.apply(blockEntity);
        if (inventory == null) {
            return null;
        }
        ResourceHandler<ItemResource> storage = VanillaContainerWrapper.of(inventory);
        if (inventory instanceof WorldlyContainer worldly) {
            @Nullable ResourceHandler<ItemResource>[] sides = new ResourceHandler[6];
            getter = side -> {
                if (side == null) {
                    return storage;
                }
                int i = side.get3DDataValue();
                ResourceHandler<ItemResource> sideStorage = sides[i];
                if (sideStorage == null) {
                    sideStorage = sides[i] = new WorldlyContainerWrapper(worldly, side);
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
