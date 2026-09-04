package dev.rohit.buglens.CorrelationEngine.model;

import java.util.ArrayList;
import java.util.List;

import dev.rohit.buglens.BLR.Bundles.CorrelationBundle;
import dev.rohit.buglens.CorrelationEngine.service.CorrelationService;
import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;
import dev.rohit.buglens.QueryLayer.service.EventQueryService;

public class CorrelationGroup {

    private final List<NormalizedEvent> allEvents;
    private final CorrelationService correlationService;
    private final CorrelationBundle bundle;
    private final ProcessingContext context;

    private List<CorrelationResult> results;

    public CorrelationGroup(
            String clientId,
            ProcessingContext context)
            throws IllegalArgumentException, IllegalAccessException {

        this.context = context;

        EventRepository repository = new EventRepository();

        EventQueryService service = new EventQueryService(repository);

        EventQuery allQuery = EventQuery.builder()
                .criteria(QueryCriteria.builder().build())
                .limit(0)
                .build();

        this.allEvents = service.execute(clientId, allQuery);

        this.correlationService = new CorrelationService(this.context);

        this.bundle = this.correlationService.getBundle();
    }

    public CorrelationBundle getBundle() {
        return this.bundle;
    }

    public List<NormalizedEvent> getEvents() {
        return this.allEvents;
    }

    public List<CorrelationResult> correlate(
            long time, CorrelationType... types) {

        this.results = new ArrayList<>();

        for (CorrelationType type : types) {

            switch (type) {

                case REQUEST_ID:
                    this.results.addAll(
                            this.bundle.groupByRequestId(allEvents));
                    break;

                case TRACE_ID:
                    this.results.addAll(
                            this.bundle.groupByTraceId(allEvents));
                    break;

                case TIME:
                    this.results.addAll(
                            this.bundle.groupByTime(
                                    allEvents,
                                    time));
                    break;

                case THREAD:
                    this.results.addAll(
                            this.bundle.groupByThread(allEvents));
                    break;

                case TRANSACTION_ID:
                    this.results.addAll(
                            this.bundle.groupByTransactionId(allEvents));
                    break;

                case SAME_COMPONENT:
                    this.results.addAll(
                            this.bundle.groupByComponent(allEvents));
                    break;

                case SAME_SERVICE:
                    this.results.addAll(
                            this.bundle.groupByService(allEvents));
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unsupported correlation type: " + type);
            }
        }

        return this.results;
    }

    public List<CorrelationResult> correlateAll(long time) {
        this.results = new ArrayList<>();

        for (CorrelationType type : this.bundle.getSupportedCorrelationTypes()) {

            this.results.addAll(
                    this.bundle.correlate(
                            this.allEvents,
                            type,
                            time));
        }

        return this.results;
    }
}