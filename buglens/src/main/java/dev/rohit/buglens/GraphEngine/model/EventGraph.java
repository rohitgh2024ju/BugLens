package dev.rohit.buglens.GraphEngine.model;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class EventGraph {

    private final String clientId;
    private final Graph<EventNode, EventRelationship> graph;

    public EventGraph(String clientId) {

        this.clientId = clientId;
        this.graph = new DefaultDirectedGraph<>(
                EventRelationship.class);
    }

    public String getClientId() {
        return clientId;
    }

    public Graph<EventNode, EventRelationship> getGraph() {
        return graph;
    }

    public void printVertices() {

        this.graph.vertexSet().forEach(node -> {

            System.out.println(
                    node.getId() + " || "
                            + node.getEvent().getTimestamp()
                            + " || "
                            + node.getEvent().getSource());
        });
    }

    public void printEdges() {

        this.graph.edgeSet().forEach(edge -> {

            EventNode source = graph.getEdgeSource(edge);

            EventNode target = graph.getEdgeTarget(edge);

            System.out.println(
                    source.getId()
                            + " -> "
                            + target.getId()
                            + " || "
                            + edge.getEvidenceTypes()
                            + " || strength : "
                            + edge.getStrength()
                            + " || confidence : "
                            + edge.getConfidence());
        });
    }
}