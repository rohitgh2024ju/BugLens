package dev.rohit.buglens.GraphEngine.service;

import java.util.List;
import java.util.Map;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.GraphEngine.model.EventRelationship;
import dev.rohit.buglens.GraphEngine.model.EvidenceScore;

public class GraphBuilder {
    private final EventGraph eventGraph;

    public GraphBuilder(EventGraph eventGraph) {
        this.eventGraph = eventGraph;
    }

    public void build(List<CorrelationResult> correlationResults, CorrelationBundle bundle) {

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

            calculationRelationshipScores(bundle);
        }
    }

    private void calculationRelationshipScores(CorrelationBundle bundle) {
        Map<CorrelationType, EvidenceScore> evidenceScores = bundle.getEvidenceScores();

        for (EventRelationship relationship : eventGraph.getGraph().edgeSet()) {
            double strengthProduct = 1.0;
            double confidenceProduct = 1.0;

            for (CorrelationType type : relationship.getEvidenceTypes()) {
                EvidenceScore score = evidenceScores.get(type);

                if (score == null) {
                    continue;
                }

                double strengthWeight = score.getStrengthWeight();

                double reliabilityWeight = score.getReliabilityWeight();

                strengthProduct *= (1 - strengthWeight);
                confidenceProduct *= (1 - reliabilityWeight * strengthWeight);

                relationship.setStrength(
                        1 - strengthProduct);

                relationship.setConfidence(
                        1 - confidenceProduct);
            }
        }
    }
}
