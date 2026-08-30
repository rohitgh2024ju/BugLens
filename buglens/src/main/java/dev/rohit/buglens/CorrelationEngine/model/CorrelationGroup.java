package dev.rohit.buglens.CorrelationEngine.model;

import java.util.ArrayList;
import java.util.List;

import dev.rohit.buglens.CorrelationEngine.service.CorrelationService;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;
import dev.rohit.buglens.QueryLayer.service.EventQueryService;

public class CorrelationGroup {

    private final List<NormalizedEvent> allEvents;
    private final CorrelationService correlationService;

    public CorrelationGroup(String clientId)
            throws IllegalArgumentException, IllegalAccessException {

        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();
        this.allEvents = service.execute(clientId, allQuery);
        this.correlationService = new CorrelationService();
    }

    public List<CorrelationResult> viewCorrelationByRequestId()
            throws IllegalArgumentException, IllegalAccessException {

        List<CorrelationResult> results = (this.correlationService).groupByRequestId(allEvents);

        results.forEach(result -> System.out
                .println(result.getType() + " || " + result.getKey() + " || " + result.getEvents()));

        return results;
    }

    public List<CorrelationResult> viewCorrelationByTraceId()
            throws IllegalArgumentException, IllegalAccessException {

        List<CorrelationResult> results = (this.correlationService).groupByTraceId(allEvents);

        results.forEach(result -> System.out
                .println(result.getType() + " || " + result.getKey() + " || " + result.getEvents()));

        return results;
    }

    public List<CorrelationResult> viewCorrelationByTime(long seconds)
            throws IllegalArgumentException, IllegalAccessException {

        List<CorrelationResult> results = (this.correlationService).groupByTime(allEvents, seconds);

        results.forEach(result -> System.out
                .println(result.getType() + " || " + result.getKey() + " || " + result.getEvents()));

        return results;
    }

    public List<CorrelationResult> correlate(long time, CorrelationType... types)
            throws IllegalArgumentException, IllegalAccessException {

        List<CorrelationResult> results = new ArrayList<>();
        for (CorrelationType type : types) {

            switch (type) {

                case REQUEST_ID:
                    results.addAll(viewCorrelationByRequestId());
                    break;

                case TRACE_ID:
                    results.addAll(viewCorrelationByTraceId());
                    break;

                case TIME:
                    results.addAll(viewCorrelationByTime(time));
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unsupported correlation type: " + type);
            }
        }

        return results;
    }
}