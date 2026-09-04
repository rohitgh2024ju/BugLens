package dev.rohit.buglens.IncidentEngine.service;

import java.util.Set;
import java.util.stream.Collectors;

import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.IncidentEngine.detector.GraphIncidentDetector;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class FailureContextBuilder {
    private final GraphIncidentDetector graphIncidentDetector;

    public FailureContextBuilder() {
        this.graphIncidentDetector = new GraphIncidentDetector();
    }

    public FailureContext build(
            EventGraph eventGraph,
            EventNode failureNode,
            double threshold) {
        Set<EventNode> connectedNodes = graphIncidentDetector.getConnectedEvents(eventGraph, failureNode, threshold);

        Set<NormalizedEvent> relatedEvents = connectedNodes.stream()
                .map(EventNode::getEvent)
                .filter(event -> !event.getId()
                        .equals(failureNode.getEvent().getId()))
                .collect(Collectors.toSet());

        return new FailureContext(
                failureNode.getEvent(),
                relatedEvents);
    }
}
