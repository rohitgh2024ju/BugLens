package dev.rohit.buglens.CorrelationEngine.model;

import java.util.List;
import java.util.Map;

import dev.rohit.buglens.CorrelationEngine.service.CorrelationService;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;
import dev.rohit.buglens.QueryLayer.service.EventQueryService;

public class CorrelationGroup {

    public Map<String, List<NormalizedEvent>> viewCorelationByReqId()
            throws IllegalArgumentException, IllegalAccessException {
        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        List<NormalizedEvent> allEvents = service.execute("000", allQuery);

        CorrelationService correlationService = new CorrelationService();
        Map<String, List<NormalizedEvent>> results = correlationService.groupByRequestId(allEvents);

        results.forEach((requestId, events) -> {

            System.out.println("\n===== Request ID: " + requestId + " =====");

            events.forEach(event -> {
                System.out.println("  " + event.getSource() + " || " + event.getTimestamp());
            });

        });

        return results;
    }

    public Map<String, List<NormalizedEvent>> viewCorelationByTraceId()
            throws IllegalArgumentException, IllegalAccessException {
        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        List<NormalizedEvent> allEvents = service.execute("000", allQuery);

        CorrelationService correlationService = new CorrelationService();
        Map<String, List<NormalizedEvent>> results = correlationService.groupByTraceId(allEvents);

        results.forEach((traceId, events) -> {

            System.out.println("\n===== Trace ID: " + traceId + " =====");

            events.forEach(event -> {
                System.out.println("  " + event.getSource() + " || " + event.getTimestamp());
            });

        });

        return results;
    }

    public List<List<NormalizedEvent>> viewCorrelationByTime(long seconds) throws IllegalArgumentException, IllegalAccessException {

        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        List<NormalizedEvent> allEvents = service.execute("000", allQuery);
        CorrelationService correlationService = new CorrelationService();

        List<List<NormalizedEvent>> results = correlationService.groupByTime(allEvents, seconds);

        for (int i = 0; i < results.size(); i++) {
            List<NormalizedEvent> group = results.get(i);
            System.out.println(
                    "\n===== Correlation Group " + (i + 1) + " =====");

            for (NormalizedEvent event : group) {
                System.out.println("  " + event.getSource() + " || " + event.getTimestamp());
            }
        }

        return results;
    }

}
