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
    private List<CorrelationResult> results;

    public CorrelationGroup(String clientId) throws IllegalArgumentException, IllegalAccessException {
        EventRepository repository = new EventRepository();
        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        this.allEvents = service.execute(clientId, allQuery);
        this.correlationService = new CorrelationService();
    }

    public List<CorrelationResult> viewCorrelationByRequestId() {
        return this.correlationService.groupByRequestId(allEvents);
    }

    public List<CorrelationResult> viewCorrelationByTraceId() {
        return this.correlationService.groupByTraceId(allEvents);
    }

    public List<CorrelationResult> viewCorrelationByTime(long seconds) {
        return this.correlationService.groupByTime(allEvents, seconds);
    }

    public List<CorrelationResult> viewCorrelationByThread() {
        return this.correlationService.groupByThread(allEvents);
    }

    public List<CorrelationResult> correlate(long time, CorrelationType... types) {
        this.results = new ArrayList<>();

        for (CorrelationType type : types) {
            switch (type) {
                case REQUEST_ID:
                    this.results.addAll(viewCorrelationByRequestId());
                    break;

                case TRACE_ID:
                    this.results.addAll(viewCorrelationByTraceId());
                    break;

                case TIME:
                    this.results.addAll(viewCorrelationByTime(time));
                    break;

                case THREAD:
                    this.results.addAll(viewCorrelationByThread());
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported correlation type: " + type);
            }
        }

        return this.results;
    }
}