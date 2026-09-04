package dev.rohit.buglens.IncidentEngine.service;

import java.util.ArrayList;
import java.util.List;

import dev.rohit.buglens.GraphEngine.model.EventGraph;
import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class FailureContextService {
    private final FailureContextBuilder failureContextBuilder;
    private List<FailureContext> contexts = new ArrayList<>();

    public FailureContextService() {
        this.failureContextBuilder = new FailureContextBuilder();
    }

    public List<FailureContext> buildAll(
            EventGraph eventGraph,
            List<NormalizedEvent> failureEvents,
            double threshold) {
        for (NormalizedEvent failureEvent : failureEvents) {

            EventNode failureNode = eventGraph.getGraph()
                    .vertexSet()
                    .stream()
                    .filter(node -> node.getId()
                            .equals(failureEvent.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Failure node not found: "
                                    + failureEvent.getId()));

            FailureContext context = failureContextBuilder.build(
                    eventGraph,
                    failureNode,
                    threshold);

            this.contexts.add(context);
        }

        return this.contexts;
    }

    public void viewAllContexts() {
        if (this.contexts == null || this.contexts.isEmpty()) {
            System.out.println("No contexts found");
            return;
        }

        this.contexts.forEach(context -> {
            System.out.println("FAILURE EVENT : " + context.getFailureEvent().getOccurrence().get("message"));
            System.out.println("SUPPORTING EVENTS : ");
            context.getRelatedEvents().forEach(event -> {
                System.out.println(event.getOccurrence().get("message"));
            });
            System.out.println("----------");
        });
    }
}
