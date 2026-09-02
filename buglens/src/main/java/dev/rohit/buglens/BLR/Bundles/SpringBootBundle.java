package dev.rohit.buglens.BLR.Bundles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import dev.rohit.buglens.GraphEngine.model.EvidenceScore;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class SpringBootBundle implements CorrelationBundle {

    @Override
    public Set<CorrelationType> getSupportedCorrelationTypes() {
        return EnumSet.of(
                CorrelationType.REQUEST_ID,
                CorrelationType.TRACE_ID,
                CorrelationType.TRANSACTION_ID,
                CorrelationType.THREAD,
                CorrelationType.TIME,
                CorrelationType.SAME_COMPONENT,
                CorrelationType.SAME_SERVICE);
    }

    @Override
    public Map<CorrelationType, EvidenceScore> getEvidenceScores() {
        Map<CorrelationType, EvidenceScore> scores = new EnumMap<>(CorrelationType.class);

        scores.put(CorrelationType.REQUEST_ID, new EvidenceScore(0.90, 0.95));
        scores.put(CorrelationType.REQUEST_ID, new EvidenceScore(0.90, 0.95));
        scores.put(CorrelationType.TRACE_ID, new EvidenceScore(0.95, 0.98));
        scores.put(CorrelationType.TRANSACTION_ID, new EvidenceScore(0.92, 0.95));
        scores.put(CorrelationType.THREAD, new EvidenceScore(0.55, 0.70));
        scores.put(CorrelationType.TIME, new EvidenceScore(0.25, 0.50));
        scores.put(CorrelationType.SAME_COMPONENT, new EvidenceScore(0.35, 0.60));
        scores.put(CorrelationType.SAME_SERVICE, new EvidenceScore(0.20, 0.40));

        return scores;
    }

    @Override
    public List<CorrelationResult> correlate(
            List<NormalizedEvent> events,
            CorrelationType type,
            long time) {

        return switch (type) {

            case REQUEST_ID ->
                groupByRequestId(events);

            case TRACE_ID ->
                groupByTraceId(events);

            case TIME ->
                groupByTime(events, time);

            case THREAD ->
                groupByThread(events);

            case TRANSACTION_ID ->
                groupByTransactionId(events);

            case SAME_COMPONENT ->
                groupByComponent(events);

            case SAME_SERVICE ->
                groupByService(events);
        };
    }

    @Override
    public List<CorrelationResult> groupByRequestId(List<NormalizedEvent> events) {
        if (events == null)
            return List.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext() != null)
                .filter(event -> event.getContext().get("requestId") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("requestId").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.REQUEST_ID, entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<CorrelationResult> groupByTraceId(List<NormalizedEvent> events) {
        if (events == null)
            return List.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext() != null)
                .filter(event -> event.getContext().get("traceId") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("traceId").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.TRACE_ID, entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<CorrelationResult> groupByTransactionId(List<NormalizedEvent> events) {
        if (events == null)
            return List.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext() != null)
                .filter(event -> event.getContext().get("transactionId") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("transactionId").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.TRANSACTION_ID, entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<CorrelationResult> groupByService(List<NormalizedEvent> events) {
        if (events == null)
            return List.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getSource() != null)
                .filter(event -> event.getSource().get("service") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getSource().get("service").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.SAME_SERVICE, entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<CorrelationResult> groupByComponent(List<NormalizedEvent> events) {
        if (events == null)
            return List.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getSource() != null)
                .filter(event -> event.getSource().get("component") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getSource().get("component").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.SAME_COMPONENT, entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<CorrelationResult> groupByThread(List<NormalizedEvent> events) {
        if (events == null) {
            return List.of();
        }

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext().get("thread") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("thread").toString()))
                .entrySet()
                .stream()
                .map(entry -> new CorrelationResult(CorrelationType.THREAD, entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public List<CorrelationResult> groupByTime(
            List<NormalizedEvent> events,
            long seconds) {

        if (seconds < 0) {
            throw new IllegalArgumentException(
                    "Time window cannot be negative");
        }

        if (events == null || events.isEmpty()) {
            return List.of();
        }

        List<NormalizedEvent> sorted = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getTimestamp() != null)
                .sorted(Comparator.comparing(NormalizedEvent::getTimestamp))
                .toList();

        if (sorted.isEmpty()) {
            return List.of();
        }

        List<CorrelationResult> results = new ArrayList<>();
        List<NormalizedEvent> currentGroup = new ArrayList<>();
        Instant groupStartTimestamp = null;
        int groupNumber = 1;

        for (NormalizedEvent event : sorted) {

            Instant currentTimestamp = event.getTimestamp();

            if (groupStartTimestamp == null ||
                    Duration.between(
                            groupStartTimestamp,
                            currentTimestamp)
                            .compareTo(Duration.ofSeconds(seconds)) <= 0) {

                if (currentGroup.isEmpty()) {
                    groupStartTimestamp = currentTimestamp;
                }
                currentGroup.add(event);

            } else {
                if (currentGroup.size() > 1) {
                    results.add(
                            new CorrelationResult(
                                    CorrelationType.TIME,
                                    "GROUP-" + groupNumber,
                                    currentGroup));

                    groupNumber++;
                }

                currentGroup = new ArrayList<>();
                currentGroup.add(event);
                groupStartTimestamp = currentTimestamp;
            }
        }
        if (currentGroup.size() > 1) {

            results.add(
                    new CorrelationResult(
                            CorrelationType.TIME,
                            "GROUP-" + groupNumber,
                            currentGroup));
        }

        return results;
    }
}
