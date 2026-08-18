package com.mohistmc.mod.module.create.content.trains.observer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.create.api.behaviour.BlockEntityBehaviour;
import com.mohistmc.mod.module.create.content.logistics.filter.FilterItemStack;
import com.mohistmc.mod.module.create.content.trains.entity.Train;
import com.mohistmc.mod.module.create.content.trains.graph.DimensionPalette;
import com.mohistmc.mod.module.create.content.trains.graph.TrackEdge;
import com.mohistmc.mod.module.create.content.trains.graph.TrackGraph;
import com.mohistmc.mod.module.create.content.trains.signal.SignalPropagator;
import com.mohistmc.mod.module.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class TrackObserver extends SingleBlockEntityEdgePoint {

    private int activated;
    private FilterItemStack filter;
    private @Nullable UUID currentTrain;

    public TrackObserver() {
        activated = 0;
        filter = FilterItemStack.empty();
        currentTrain = null;
    }

    @Override
    public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
        super.blockEntityAdded(blockEntity, front);
        ServerFilteringBehaviour filteringBehaviour = BlockEntityBehaviour.get(
            blockEntity,
            ServerFilteringBehaviour.TYPE
        );
        if (filteringBehaviour != null) {
            setFilterAndNotify(blockEntity.getLevel(), filteringBehaviour.getFilter());
        }
    }

    @Override
    public void tick(MinecraftServer server, TrackGraph graph, boolean preTrains) {
        super.tick(server, graph, preTrains);
        if (isActivated()) {
            activated--;
        }
        if (!isActivated()) {
            currentTrain = null;
        }
    }

    public void setFilterAndNotify(Level level, ItemStack filter) {
        this.filter = FilterItemStack.of(filter.copy());
        notifyTrains(level);
    }

    private void notifyTrains(Level level) {
        TrackGraph graph = Create.RAILWAYS.sided(level).getGraph(edgeLocation.getFirst());
        if (graph == null) {
            return;
        }
        TrackEdge edge = graph.getConnection(edgeLocation.map(graph::locateNode));
        if (edge == null) {
            return;
        }
        SignalPropagator.notifyTrains(graph, edge);
    }

    public FilterItemStack getFilter() {
        return filter;
    }

    @Nullable
    public UUID getCurrentTrain() {
        return currentTrain;
    }

    public boolean isActivated() {
        return activated > 0;
    }

    public void keepAlive(Train train) {
        activated = 8;
        currentTrain = train.id;
    }

    @Override
    public void read(ValueInput view, boolean migration, DimensionPalette dimensions) {
        super.read(view, migration, dimensions);
        activated = view.getIntOr("Activated", 0);
        filter = view.read("Filter", FilterItemStack.CODEC).orElseGet(FilterItemStack::empty);
        currentTrain = view.read("TrainId", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    public <T> void decode(DynamicOps<T> ops, T input, boolean migration, DimensionPalette dimensions) {
        super.decode(ops, input, migration, dimensions);
        MapLike<T> map = ops.getMap(input).getOrThrow();
        activated = ops.getNumberValue(map.get("Activated")).getOrThrow().intValue();
        filter = FilterItemStack.CODEC.parse(ops, map.get("Filter")).result().orElseGet(FilterItemStack::empty);
        currentTrain = UUIDUtil.CODEC.parse(ops, map.get("TrainId")).result().orElse(null);
    }

    @Override
    public void write(ValueOutput view, DimensionPalette dimensions) {
        super.write(view, dimensions);
        view.putInt("Activated", activated);
        view.store("Filter", FilterItemStack.CODEC, filter);
        if (currentTrain != null) {
            view.store("TrainId", UUIDUtil.CODEC, currentTrain);
        }
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops, T empty, DimensionPalette dimensions) {
        DataResult<T> prefix = super.encode(ops, empty, dimensions);
        RecordBuilder<T> map = ops.mapBuilder();
        map.add("Activated", ops.createInt(activated));
        map.add("Filter", filter, FilterItemStack.CODEC);
        if (currentTrain != null) {
            map.add("TrainId", currentTrain, UUIDUtil.CODEC);
        }
        return map.build(prefix);
    }
}
