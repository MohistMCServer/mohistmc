package com.mohistmc.mod.module.create.content.trains.graph;

import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.create.catnip.data.Couple;
import com.mohistmc.mod.module.create.catnip.data.Iterate;
import com.mohistmc.mod.module.create.content.trains.signal.TrackEdgePoint;
import net.minecraft.server.MinecraftServer;

public class EdgePointManager {

    public static <T extends TrackEdgePoint> void onEdgePointAdded(
        MinecraftServer server,
        TrackGraph graph,
        T point,
        EdgePointType<T> type
    ) {
        Couple<TrackNodeLocation> edgeLocation = point.edgeLocation;
        Couple<TrackNode> startNodes = edgeLocation.map(graph::locateNode);
        Couple<TrackEdge> startEdges = startNodes.mapWithParams(
            (l1, l2) -> graph.getConnectionsFrom(l1).get(l2),
            startNodes.swap()
        );

        for (boolean front : Iterate.trueAndFalse) {
            TrackNode node1 = startNodes.get(front);
            TrackNode node2 = startNodes.get(!front);
            TrackEdge startEdge = startEdges.get(front);
            startEdge.getEdgeData().addPoint(server, graph, point);
            Create.RAILWAYS.sync.edgeDataChanged(graph, node1, node2, startEdge);
        }
    }

    public static <T extends TrackEdgePoint> void onEdgePointRemoved(
        MinecraftServer server,
        TrackGraph graph,
        T point,
        EdgePointType<T> type
    ) {
        point.onRemoved(server, graph);
        Couple<TrackNodeLocation> edgeLocation = point.edgeLocation;
        Couple<TrackNode> startNodes = edgeLocation.map(graph::locateNode);
        startNodes.forEachWithParams(
            (l1, l2) -> {
                TrackEdge trackEdge = graph.getConnectionsFrom(l1).get(l2);
                if (trackEdge == null) {
                    return;
                }
                trackEdge.getEdgeData().removePoint(server, graph, point);
                Create.RAILWAYS.sync.edgeDataChanged(graph, l1, l2, trackEdge);
            }, startNodes.swap()
        );
    }

}
