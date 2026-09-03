package dev.rohit.buglens.GraphEngine.service;

import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.repository.GraphRepository;

public class GraphPersistenceService {

    private final String clientId;

    private final GraphRepository graphRepository;

    public GraphPersistenceService(String clientId) {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "clientId is required");
        }

        this.clientId = clientId;

        this.graphRepository = new GraphRepository();
    }

    public void save(EventGraph graph) {

        if (graph == null) {
            throw new IllegalArgumentException(
                    "graph is required");
        }

        graphRepository.save(
                clientId,
                graph);
    }

    public EventGraph load() throws IllegalAccessException {
        return graphRepository.load(clientId);
    }
}