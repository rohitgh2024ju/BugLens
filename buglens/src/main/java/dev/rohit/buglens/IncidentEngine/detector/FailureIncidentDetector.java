package dev.rohit.buglens.IncidentEngine.detector;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import dev.rohit.buglens.GraphEngine.model.EventNode;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;
import dev.rohit.buglens.QueryLayer.service.EventQueryService;

public class FailureIncidentDetector {

    private static final Set<String> FAILURE_KEYWORDS = Set.of(
            "error",
            "exception",
            "failed",
            "failure",
            "timeout",
            "timed out",
            "refused",
            "unavailable",
            "denied");

    private String clientId;
    private List<NormalizedEvent> allEvents;

    public FailureIncidentDetector(String clientId) throws IllegalArgumentException, IllegalAccessException {
        this.clientId = clientId;

        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        this.allEvents = service.execute(clientId, allQuery);
    }

    public boolean isFailure(EventNode node) {
        if (node == null || node.getEvent() == null) {
            return false;
        }

        Object message = node.getEvent().getOccurrence().get("message");

        if (message == null) {
            return false;
        }

        String text = message.toString().toLowerCase();
        return FAILURE_KEYWORDS.stream().anyMatch(text::contains);
    }

    public List<String> detectFailureEventIds() {
        return this.allEvents.stream()
                .filter(event -> {
                    EventNode node = EventNode.builder()
                            .id(event.getId())
                            .event(event)
                            .build();

                    return isFailure(node);
                })
                .map(NormalizedEvent::getId)
                .toList();
    }

    public List<NormalizedEvent> detectFailureEvent(
            List<String> failureIdList) {

        return failureIdList.stream()
                .map(id -> allEvents.stream()
                        .filter(event -> id.equals(event.getId()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}
