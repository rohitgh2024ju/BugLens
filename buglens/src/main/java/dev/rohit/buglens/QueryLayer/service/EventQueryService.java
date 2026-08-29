package dev.rohit.buglens.QueryLayer.service;

import java.util.List;
import java.util.Map;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

public class EventQueryService {
    private final EventRepository eventRepository;

    public EventQueryService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<NormalizedEvent> execute(
            String clientId, EventQuery query) throws IllegalArgumentException, IllegalAccessException {
        return eventRepository.find(clientId, query);
    }

    public void executeView(
            String clientId) throws IllegalArgumentException, IllegalAccessException {
        eventRepository.viewAll(clientId);
    }

    public static void main(String[] args)
            throws IllegalArgumentException, IllegalAccessException {

        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        // =========================================================
        // 1. GET ALL EVENTS
        // =========================================================

        System.out.println("\n===== TEST 1: ALL EVENTS =====");

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        List<NormalizedEvent> allEvents = service.execute("000", allQuery);

        System.out.println("Total events: " + allEvents.size());

        // =========================================================
        // 2. FILTER BY OCCURRENCE
        // occurrence.severity = ERROR
        // =========================================================

        System.out.println("\n===== TEST 2: SEVERITY = ERROR =====");

        QueryCriteria errorCriteria = QueryCriteria.builder()
                .occurrence(Map.of(
                        "severity", "ERROR"))
                .build();

        EventQuery errorQuery = EventQuery.builder()
                .criteria(errorCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> errorEvents = service.execute("000", errorQuery);

        errorEvents.forEach(System.out::println);

        System.out.println("ERROR events: " + errorEvents.size());

        // =========================================================
        // 3. FILTER BY SOURCE
        // source.component = PaymentService
        // =========================================================

        System.out.println("\n===== TEST 3: SOURCE COMPONENT =====");

        QueryCriteria sourceCriteria = QueryCriteria.builder()
                .source(Map.of(
                        "component",
                        "com.buglens.payment.PaymentService"))
                .build();

        EventQuery sourceQuery = EventQuery.builder()
                .criteria(sourceCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> sourceEvents = service.execute("000", sourceQuery);

        sourceEvents.forEach(System.out::println);

        System.out.println("Matching events: " + sourceEvents.size());

        // =========================================================
        // 4. FILTER BY CONTEXT
        // context.requestId = REQ-1001
        // =========================================================

        System.out.println("\n===== TEST 4: REQUEST ID = REQ-1001 =====");

        QueryCriteria requestCriteria = QueryCriteria.builder()
                .context(Map.of(
                        "requestId", "REQ-1001"))
                .build();

        EventQuery requestQuery = EventQuery.builder()
                .criteria(requestCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> requestEvents = service.execute("000", requestQuery);

        requestEvents.forEach(System.out::println);

        System.out.println("Matching events: " + requestEvents.size());

        // =========================================================
        // 5. FILTER BY METADATA
        // metadata.amount = 1499.00
        // =========================================================

        System.out.println("\n===== TEST 5: AMOUNT = 1499.00 =====");

        QueryCriteria amountCriteria = QueryCriteria.builder()
                .metadata(Map.of(
                        "amount", 1499.00))
                .build();

        EventQuery amountQuery = EventQuery.builder()
                .criteria(amountCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> amountEvents = service.execute("000", amountQuery);

        amountEvents.forEach(System.out::println);

        System.out.println("Matching events: " + amountEvents.size());

        // =========================================================
        // 6. MULTIPLE CONDITIONS
        // severity = ERROR AND requestId = REQ-1001
        // =========================================================

        System.out.println("\n===== TEST 6: ERROR + REQ-1001 =====");

        QueryCriteria combinedCriteria = QueryCriteria.builder()
                .occurrence(Map.of(
                        "severity", "ERROR"))
                .context(Map.of(
                        "requestId", "REQ-1001"))
                .build();

        EventQuery combinedQuery = EventQuery.builder()
                .criteria(combinedCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> combinedEvents = service.execute("000", combinedQuery);

        combinedEvents.forEach(System.out::println);

        System.out.println("Matching events: " + combinedEvents.size());

        // =========================================================
        // 7. LIMIT
        // =========================================================

        System.out.println("\n===== TEST 7: LIMIT = 2 =====");

        EventQuery limitedQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder()
                        .occurrence(Map.of(
                                "severity", "ERROR"))
                        .build())
                .limit(2)
                .build();

        List<NormalizedEvent> limitedEvents = service.execute("000", limitedQuery);

        limitedEvents.forEach(System.out::println);

        System.out.println("Returned events: " + limitedEvents.size());

        // =========================================================
        // 8. NO MATCH
        // =========================================================

        System.out.println("\n===== TEST 8: NO MATCH =====");

        QueryCriteria noMatchCriteria = QueryCriteria.builder()
                .occurrence(Map.of(
                        "severity", "CRITICAL"))
                .build();

        EventQuery noMatchQuery = EventQuery.builder()
                .criteria(noMatchCriteria)
                .limit(10)
                .build();

        List<NormalizedEvent> noMatchEvents = service.execute("000", noMatchQuery);

        System.out.println("Returned events: " + noMatchEvents.size());

        System.out.println("\n===== ALL QUERY TESTS COMPLETED =====");
    }
}
