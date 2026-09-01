package dev.rohit.buglens.GraphEngine.service;

import java.util.List;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.GraphEngine.model.EventRelationship;

public class GraphBuilder {
    private final EventGraph eventGraph;

    public GraphBuilder(EventGraph eventGraph) {
        this.eventGraph = eventGraph;
    }

    public void build(List<CorrelationResult> correlationResults) {

        if (correlationResults == null || correlationResults.isEmpty()) {
            return;
        }

        for (CorrelationResult result : correlationResults) {

            List<NormalizedEvent> events = result.getEvents();

            if (events == null || events.size() < 2) {
                continue;
            }

            EventNode previousNode = null;

            for (NormalizedEvent event : events) {

                EventNode currentNode = EventNode.builder()
                        .id(event.getId().toString())
                        .event(event)
                        .build();

                eventGraph.getGraph().addVertex(currentNode);

                if (previousNode != null) {

                    EventRelationship existingEdge = eventGraph.getGraph().getEdge(previousNode, currentNode);

                    if (existingEdge != null) {
                        existingEdge.getEvidenceTypes().add(result.getType());
                    } else {

                        EventRelationship relationship = new EventRelationship();
                        relationship.getEvidenceTypes().add(result.getType());
                        
                        eventGraph.getGraph().addEdge(
                                previousNode,
                                currentNode, relationship);
                    }

                }

                previousNode = currentNode;
            }
        }
    }
}
