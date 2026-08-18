package com.mohistmc.mod.module.create.content.trains;

import com.mohistmc.mod.module.create.AllClientHandle;
import com.mohistmc.mod.module.create.content.trains.display.GlobalTrainDisplayData;
import com.mohistmc.mod.module.create.content.trains.entity.Train;
import com.mohistmc.mod.module.create.content.trains.graph.TrackGraph;
import com.mohistmc.mod.module.create.content.trains.graph.TrackGraphSync;
import com.mohistmc.mod.module.create.content.trains.graph.TrackNodeLocation;
import com.mohistmc.mod.module.create.content.trains.signal.EdgeGroupColor;
import com.mohistmc.mod.module.create.content.trains.signal.SignalEdgeGroup;
import com.mohistmc.mod.module.create.infrastructure.packet.s2c.AddTrainPacket;
import com.mohistmc.mod.module.create.infrastructure.packet.s2c.RemoveTrainPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jspecify.annotations.Nullable;

public class GlobalRailwayManager {

    public Map<UUID, TrackGraph> trackNetworks;
    public Map<UUID, SignalEdgeGroup> signalEdgeGroups;
    public Map<UUID, Train> trains;
    public TrackGraphSync sync;

    private List<Train> movingTrains;
    private List<Train> waitingTrains;

    private @Nullable RailwaySavedData savedData;

    public int version;

    public GlobalRailwayManager() {
        cleanUp();
    }

    public void playerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            loadTrackData(serverPlayer.level().getServer());
            for (TrackGraph g : trackNetworks.values()) {
                sync.sendFullGraphTo(g, serverPlayer);
            }

            List<UUID> ids = new ArrayList<>(signalEdgeGroups.size());
            List<EdgeGroupColor> colors = new ArrayList<>(signalEdgeGroups.size());
            for (SignalEdgeGroup group : signalEdgeGroups.values()) {
                ids.add(group.id);
                colors.add(group.color);
            }
            sync.sendEdgeGroups(ids, colors, serverPlayer);
            for (Train train : trains.values()) {
                serverPlayer.connection.send(new AddTrainPacket(train));
            }
        }
    }

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        cleanUp();
        savedData = null;
        loadTrackData(server);
    }

    private void loadTrackData(MinecraftServer server) {
        if (savedData != null) {
            return;
        }
        savedData = RailwaySavedData.load(server);
        trains = savedData.getTrains();
        trackNetworks = savedData.getTrackNetworks();
        signalEdgeGroups = savedData.getSignalBlocks();
        movingTrains.addAll(trains.values());
    }

    public void cleanUp() {
        trackNetworks = new HashMap<>();
        signalEdgeGroups = new HashMap<>();
        trains = new HashMap<>();
        sync = new TrackGraphSync();
        movingTrains = new LinkedList<>();
        waitingTrains = new LinkedList<>();
        GlobalTrainDisplayData.statusByDestination.clear();
    }

    public void markTracksDirty() {
        if (savedData != null) {
            savedData.setDirty();
        }
    }

    public void addTrain(Train train) {
        trains.put(train.id, train);
        movingTrains.add(train);
    }

    public void removeTrain(UUID id) {
        Train removed = trains.remove(id);
        if (removed == null) {
            return;
        }
        movingTrains.remove(removed);
        waitingTrains.remove(removed);
    }

    //

    public TrackGraph getOrCreateGraph(UUID graphID, int netId) {
        return trackNetworks.computeIfAbsent(
            graphID, uid -> {
                TrackGraph trackGraph = new TrackGraph(graphID);
                trackGraph.setNetId(netId);
                return trackGraph;
            }
        );
    }

    public void putGraphWithDefaultGroup(MinecraftServer server, TrackGraph graph) {
        SignalEdgeGroup group = new SignalEdgeGroup(graph.id);
        signalEdgeGroups.put(graph.id, group.asFallback());
        sync.edgeGroupCreated(server, graph.id, group.color);
        putGraph(graph);
    }

    public void putGraph(TrackGraph graph) {
        trackNetworks.put(graph.id, graph);
        markTracksDirty();
    }

    public void removeGraphAndGroup(MinecraftServer server, TrackGraph graph) {
        signalEdgeGroups.remove(graph.id);
        sync.edgeGroupRemoved(server, graph.id);
        removeGraph(graph);
    }

    public void removeGraph(TrackGraph graph) {
        trackNetworks.remove(graph.id);
        markTracksDirty();
    }

    public void updateSplitGraph(LevelAccessor level, TrackGraph graph) {
        Set<TrackGraph> disconnected = graph.findDisconnectedGraphs(level, null);
        MinecraftServer server = level.getServer();
        for (TrackGraph d : disconnected) {
            putGraphWithDefaultGroup(server, d);
        }
        if (!disconnected.isEmpty()) {
            sync.graphSplit(graph, disconnected);
            markTracksDirty();
        }
    }

    @Nullable
    public TrackGraph getGraph(TrackNodeLocation vertex) {
        if (trackNetworks == null) {
            return null;
        }
        for (TrackGraph railGraph : trackNetworks.values()) {
            if (railGraph.locateNode(vertex) != null) {
                return railGraph;
            }
        }
        return null;
    }

    public List<TrackGraph> getGraphs(TrackNodeLocation vertex) {
        if (trackNetworks == null) {
            return Collections.emptyList();
        }
        ArrayList<TrackGraph> intersecting = new ArrayList<>();
        for (TrackGraph railGraph : trackNetworks.values()) {
            if (railGraph.locateNode(vertex) != null) {
                intersecting.add(railGraph);
            }
        }
        return intersecting;
    }

    public void tick(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        for (SignalEdgeGroup group : signalEdgeGroups.values()) {
            group.trains.clear();
            group.reserved = null;
        }

        MinecraftServer server = level.getServer();
        for (TrackGraph graph : trackNetworks.values()) {
            graph.tickPoints(server, true);
            graph.resolveIntersectingEdgeGroups(level);
        }

        tickTrains(level);

        for (TrackGraph graph : trackNetworks.values()) {
            graph.tickPoints(server, false);
        }

        GlobalTrainDisplayData.updateTick = level.getGameTime() % 100 == 0;
        if (GlobalTrainDisplayData.updateTick) {
            GlobalTrainDisplayData.refresh();
        }

        //		if (AllKeys.isKeyDown(GLFW.GLFW_KEY_H) && AllKeys.altDown())
        //			for (TrackGraph trackGraph : trackNetworks.values())
        //				TrackGraphVisualizer.debugViewSignalData(trackGraph);
        //		if (AllKeys.isKeyDown(GLFW.GLFW_KEY_J) && AllKeys.altDown())
        //			for (TrackGraph trackGraph : trackNetworks.values())
        //				TrackGraphVisualizer.debugViewNodes(trackGraph);
    }

    private void tickTrains(Level level) {
        // keeping two lists ensures a tick order starting at longest waiting
        for (Train train : waitingTrains) {
            train.earlyTick(level);
        }
        for (Train train : movingTrains) {
            train.earlyTick(level);
        }
        for (Train train : waitingTrains) {
            train.tick(level);
        }
        for (Train train : movingTrains) {
            train.tick(level);
        }

        PlayerList playerManager = level.getServer().getPlayerList();
        for (Iterator<Train> iterator = waitingTrains.iterator(); iterator.hasNext(); ) {
            Train train = iterator.next();

            if (train.invalid) {
                iterator.remove();
                trains.remove(train.id);
                playerManager.broadcastAll(new RemoveTrainPacket(train));
                continue;
            }

            if (train.navigation.waitingForSignal != null) {
                continue;
            }
            movingTrains.add(train);
            iterator.remove();
        }

        for (Iterator<Train> iterator = movingTrains.iterator(); iterator.hasNext(); ) {
            Train train = iterator.next();

            if (train.invalid) {
                iterator.remove();
                trains.remove(train.id);
                playerManager.broadcastAll(new RemoveTrainPacket(train));
                continue;
            }

            if (train.navigation.waitingForSignal == null) {
                continue;
            }
            waitingTrains.add(train);
            iterator.remove();
        }

    }

    public GlobalRailwayManager sided(@Nullable LevelAccessor level) {
        if (level != null && !level.isClientSide()) {
            return this;
        }
        return AllClientHandle.INSTANCE.getGlobalRailwayManager();
    }

}
