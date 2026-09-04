package dev.rohit.buglens.IncidentEngine.detector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.GraphEngine.model.EventRelationship;

public class GraphIncidentDetector {
    
    public Set<EventNode> getConnectedEvents(EventGraph eventGraph,
            EventNode startNode,
            double threshold) {
        if (threshold < 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1");
        }
        Set<EventNode> visited = new HashSet<>();
        Queue<EventNode> queue = new LinkedList<>();

        visited.add(startNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            EventNode current = queue.poll();

            for (EventRelationship edge : eventGraph.getGraph().edgesOf(current)) {
                if (edge.getStrength() < threshold) {
                    continue;
                }
                EventNode source = eventGraph.getGraph().getEdgeSource(edge);
                EventNode target = eventGraph.getGraph().getEdgeTarget(edge);

                EventNode neighbor = source.equals(current) ? target : source;

                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }
}
