package dev.rohit.buglens.GraphEngine.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.ObjectRepository;

import org.dizitart.no2.filters.Filter;
import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.GraphEngine.model.EventRelationship;
import dev.rohit.buglens.GraphEngine.model.StoredGraphEdge;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

public class GraphRepository {

    public void save(String clientId, EventGraph eventGraph) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId is required");
        }
        if (eventGraph == null) {
            throw new IllegalArgumentException("eventGraph is required");
        }

        Nitrite db = NitriteMultiTenantConfig.getDatabaseForClient(clientId);
        ObjectRepository<StoredGraphEdge> repository = db.getRepository(StoredGraphEdge.class);

        repository.remove(Filter.ALL);

        for (EventRelationship relationship : eventGraph.getGraph().edgeSet()) {
            EventNode source = eventGraph.getGraph()
                    .getEdgeSource(relationship);

            EventNode target = eventGraph.getGraph()
                    .getEdgeTarget(relationship);

            StoredGraphEdge storedEdge = StoredGraphEdge.builder()
                    .sourceNodeId(source.getId())
                    .targetNodeId(target.getId())
                    .evidenceTypes(
                            relationship.getEvidenceTypes())
                    .strength(
                            relationship.getStrength())
                    .confidence(
                            relationship.getConfidence())
                    .build();
            repository.insert(storedEdge);
        }

    }

    public EventGraph load(String clientId)
            throws IllegalAccessException {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "clientId is required");
        }

        // Open client's database
        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        // Load stored graph edges
        ObjectRepository<StoredGraphEdge> graphRepository = db.getRepository(StoredGraphEdge.class);

        List<StoredGraphEdge> storedEdges = graphRepository.find().toList();

        // Create runtime graph
        EventGraph eventGraph = new EventGraph(clientId);

        if (storedEdges.isEmpty()) {
            return eventGraph;
        }

        // Load normalized events
        EventRepository eventRepository = new EventRepository();

        List<NormalizedEvent> events = eventRepository.getAll(clientId);

        // Create event lookup map
        Map<String, NormalizedEvent> eventMap = events.stream()
                .collect(Collectors.toMap(
                        event -> event.getId().toString(),
                        event -> event));

        // Reconstruct graph
        for (StoredGraphEdge storedEdge : storedEdges) {

            NormalizedEvent sourceEvent = eventMap.get(
                    storedEdge.getSourceNodeId());

            NormalizedEvent targetEvent = eventMap.get(
                    storedEdge.getTargetNodeId());

            // Skip broken edges
            if (sourceEvent == null ||
                    targetEvent == null) {
                continue;
            }

            EventNode sourceNode = EventNode.builder()
                    .id(sourceEvent.getId().toString())
                    .event(sourceEvent)
                    .build();

            EventNode targetNode = EventNode.builder()
                    .id(targetEvent.getId().toString())
                    .event(targetEvent)
                    .build();

            EventRelationship relationship = EventRelationship.builder()
                    .evidenceTypes(
                            storedEdge.getEvidenceTypes())
                    .strength(
                            storedEdge.getStrength())
                    .confidence(
                            storedEdge.getConfidence())
                    .build();

            eventGraph.getGraph()
                    .addVertex(sourceNode);

            eventGraph.getGraph()
                    .addVertex(targetNode);

            eventGraph.getGraph()
                    .addEdge(
                            sourceNode,
                            targetNode,
                            relationship);
        }

        return eventGraph;
    }
}
