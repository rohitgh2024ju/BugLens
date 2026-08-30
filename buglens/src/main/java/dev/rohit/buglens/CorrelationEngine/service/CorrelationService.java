package dev.rohit.buglens.CorrelationEngine.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class CorrelationService {

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